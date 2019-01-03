package io.openfuture.chain.core.sync

import io.openfuture.chain.consensus.property.ConsensusProperties
import io.openfuture.chain.core.model.entity.block.Block
import io.openfuture.chain.core.model.entity.block.GenesisBlock
import io.openfuture.chain.core.model.entity.block.MainBlock
import io.openfuture.chain.core.service.BlockService
import io.openfuture.chain.core.service.DelegateService
import io.openfuture.chain.core.service.block.DefaultMainBlockService
import io.openfuture.chain.core.sync.SyncStatus.*
import io.openfuture.chain.crypto.util.HashUtils
import io.openfuture.chain.network.component.AddressesHolder
import io.openfuture.chain.network.component.ChannelsHolder
import io.openfuture.chain.network.component.time.Clock
import io.openfuture.chain.network.entity.NetworkAddress
import io.openfuture.chain.network.entity.NodeInfo
import io.openfuture.chain.network.message.sync.EpochRequestMessage
import io.openfuture.chain.network.message.sync.EpochResponseMessage
import io.openfuture.chain.network.message.sync.GenesisBlockMessage
import io.openfuture.chain.network.message.sync.SyncRequestMessage
import io.openfuture.chain.network.property.NodeProperties
import io.openfuture.chain.network.serialization.Serializable
import io.openfuture.chain.network.service.NetworkApiService
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.net.InetAddress

@Component
class SyncManager(
    private val clock: Clock,
    private val blockService: BlockService,
    private val properties: NodeProperties,
    private val channelsHolder: ChannelsHolder,
    private val addressesHolder: AddressesHolder,
    private val delegateService: DelegateService,
    private val networkApiService: NetworkApiService,
    private val consensusProperties: ConsensusProperties,
    private val currentGenesisBlock: CurrentGenesisBlock,
    private val mainBlockService: DefaultMainBlockService
) {

    companion object {
        private val log: Logger = LoggerFactory.getLogger(SyncManager::class.java)
    }

    @Volatile
    private var lastResponseTime: Long = 0L

    @Volatile
    private var status: SyncStatus = SYNCHRONIZED

    @Volatile
    private var activeDelegateNodeInfo: NodeInfo? = null

    @Volatile
    private lateinit var delegateNodeInfo: List<NodeInfo>

    @Volatile
    private var cursorGenesisBlock: GenesisBlock? = null

    /**
     * Latest genesis block from delegates
     * */
    @Volatile
    private var latestGenesisBlock: GenesisBlock? = null

    /**
     * Native genesis block on start up
     * */
    @Volatile
    var currentLastGenesisBlock: GenesisBlock = currentGenesisBlock.block

    fun getStatus(): SyncStatus = status

    fun resetCursorAndLastBlock() {
        this.cursorGenesisBlock = null
        this.latestGenesisBlock = null
    }

    @Scheduled(fixedRateString = "\${node.sync-interval}")
    fun syncBlock() {
        if (status != SYNCHRONIZED) {
            log.debug("Ledger in $status")
            sync()
        }
    }

    @Synchronized
    fun outOfSync(publicKey: String) {
        if (PROCESSING != status) {
            status = NOT_SYNCHRONIZED
            log.debug("set NOT_SYNCHRONIZED in outOfSync")

        }
        if (null == activeDelegateNodeInfo) {
            val uid = ByteUtils.toHexString(HashUtils.sha256(ByteUtils.fromHexString(publicKey)))
            activeDelegateNodeInfo = addressesHolder.getNodeInfos().firstOrNull { it.uid == uid }
        }
    }

    @Synchronized
    fun sync() {
        if (NOT_SYNCHRONIZED == status && null == latestGenesisBlock) {
            status = PROCESSING
            networkApiService.sendToAddress(SyncRequestMessage(), activeDelegateNodeInfo!!)
            return
        }

        if (status == PROCESSING && clock.currentTimeMillis() >= lastResponseTime) {
            when {
                null == cursorGenesisBlock -> {
                    log.debug("####AGAIN SyncRequestMessage")
                    status = PROCESSING
                    networkApiService.sendToAddress(SyncRequestMessage(), activeDelegateNodeInfo!!)
                }
                currentLastGenesisBlock.hash != cursorGenesisBlock!!.hash -> sendMessageToRandomDelegate(
                    EpochRequestMessage(cursorGenesisBlock!!.payload.epochIndex - 1), delegateNodeInfo)
                else -> resetCursorAndLastBlock()
            }
            return
        }

        if (NOT_SYNCHRONIZED == status && null == latestGenesisBlock) {
            status = PROCESSING
            channelsHolder.send(SyncRequestMessage(), activeDelegateNodeInfo!!)
        }
    }

    fun setReceivedLastGenesisBlock(receivedLastGenesisBlock: GenesisBlockMessage) {
        val block = GenesisBlock.of(receivedLastGenesisBlock, delegateService)

        if (block.hash == currentLastGenesisBlock.hash) {
            latestGenesisBlock = block
            cursorGenesisBlock = block
            val nodesInfo = currentLastGenesisBlock.payload
                .activeDelegates.map { NodeInfo(it.nodeId, NetworkAddress(it.host, it.port)) }
            sendMessageToRandomDelegate(EpochRequestMessage(block.payload.epochIndex), nodesInfo)
            return
        }

        if (latestGenesisBlock == null) {
            cursorGenesisBlock = block
            latestGenesisBlock = block
            delegateNodeInfo = block.payload
                .activeDelegates.map { NodeInfo(it.nodeId, NetworkAddress(it.host, it.port)) }

            blockService.saveUnique(block)
            sendMessageToRandomDelegate(EpochRequestMessage(block.payload.epochIndex - 1), delegateNodeInfo)
        }
    }

    fun epochResponse(address: InetAddress, msg: EpochResponseMessage) {
        latestGenesisBlock ?: return

        lastResponseTime = clock.currentTimeMillis() + properties.expiry!!

        if (!msg.isEpochExists) {
            sendEpochRequestToFilteredNodes(cursorGenesisBlock!!.payload.epochIndex, address, msg.nodeId)
            return
        }

        val genesisBlockMessage = msg.genesisBlock!!
        val genesisBlock = GenesisBlock.of(genesisBlockMessage, delegateService)
        val mainBlockMessages = msg.mainBlocks
        if (mainBlockMessages.isEmpty() && isLatestEpochForSync(genesisBlockMessage.hash, address, msg.nodeId)) {
            setSynchronized()
            return
        }

        epochSync(msg, genesisBlock, address)
    }

    private fun epochSync(msg: EpochResponseMessage, genesisBlock: GenesisBlock, address: InetAddress) {
        val mainBlockMessages = msg.mainBlocks
        val mainBlocks = mainBlockMessages.map { MainBlock.of(it) }

        if (mainBlocks.size == consensusProperties.epochHeight!!) {
            if (!isValidBlocks(genesisBlock, mainBlocks, cursorGenesisBlock!!)) {
                sendEpochRequestToFilteredNodes(genesisBlock.payload.epochIndex, address, msg.nodeId)
                return
            }

            mainBlockService.saveUniqueBlocks(mainBlocks)
            blockService.saveUnique(genesisBlock)
            if (currentLastGenesisBlock.hash != genesisBlock.hash) {
                cursorGenesisBlock = genesisBlock
                sendMessageToRandomDelegate(EpochRequestMessage(cursorGenesisBlock!!.payload.epochIndex - 1), delegateNodeInfo)
            } else {
                currentLastGenesisBlock = latestGenesisBlock!!
                resetCursorAndLastBlock()
            }
        } else if (!isItActiveDelegate(address, msg.nodeId)) {
            sendEpochRequestToFilteredNodes(genesisBlock.payload.epochIndex, address, msg.nodeId)
        } else if (isLatestEpochForSync(genesisBlock.hash, address, msg.nodeId) && isValidBlocks(genesisBlock, mainBlocks)) {
            mainBlockService.saveUniqueBlocks(mainBlocks)
            setSynchronized()
        }
    }

    @Synchronized
    private fun setSynchronized() {
        currentGenesisBlock.block = currentLastGenesisBlock
        resetCursorAndLastBlock()
        activeDelegateNodeInfo = null
        status = SYNCHRONIZED
        lastResponseTime = 0L
    }

    private fun getFilteredNodesInfo(address: InetAddress, nodeId: String): List<NodeInfo> =
        delegateNodeInfo.filter { it.address.host != address.hostAddress && it.uid != nodeId }

    private fun sendEpochRequestToFilteredNodes(epochIndex: Long, address: InetAddress, nodeId: String) {
        val nodesInfo = getFilteredNodesInfo(address, nodeId)
        sendMessageToRandomDelegate(EpochRequestMessage(epochIndex), nodesInfo)
    }

    private fun sendMessageToRandomDelegate(message: Serializable, nodesInfo: List<NodeInfo>): Boolean {
        if (nodesInfo.isEmpty()) return false

        lastResponseTime = clock.currentTimeMillis() + properties.syncExpiry!!

        val shuffledNodesInfo = nodesInfo.shuffled()
        for (nodeInfo in shuffledNodesInfo) {
            if (networkApiService.sendToAddress(message, nodeInfo)) {
                log.debug("Send ${message::class.java} request to ${nodeInfo.address} ")
                return true
            }
        }
        return false
    }

    private fun isItActiveDelegate(address: InetAddress, nodeId: String): Boolean =
        latestGenesisBlock!!.payload
            .activeDelegates.any { it.host == address.hostAddress && it.nodeId == nodeId }


    private fun isLatestEpochForSync(receivedHash: String, address: InetAddress, nodeId: String): Boolean =
        receivedHash == currentLastGenesisBlock.hash && isItActiveDelegate(address, nodeId)

    private fun isValidBlocks(genesisBlock: GenesisBlock, mainBlocks: List<MainBlock>): Boolean {
        val blocks = ArrayList<Block>(mainBlocks.size + 1)
        blocks.add(genesisBlock)
        blocks.addAll(mainBlocks)
        val blockIterator = blocks.iterator()
        return isValidBlocks(blockIterator, true)
    }

    private fun isValidBlocks(
        genesisBlock: GenesisBlock,
        mainBlocks: List<MainBlock>,
        lastGenesisBlock: GenesisBlock
    ): Boolean {
        val blocks = ArrayList<Block>(mainBlocks.size + 2)
        blocks.add(genesisBlock)
        blocks.addAll(mainBlocks)
        blocks.add(lastGenesisBlock)
        val blockIterator = blocks.reversed().iterator()
        return isValidBlocks(blockIterator, false)
    }

    private fun isValidBlocks(blockIterator: Iterator<Block>, isStraight: Boolean): Boolean {
        var currentMainBlock = blockIterator.next()
        while (blockIterator.hasNext()) {
            val nextBlock = blockIterator.next()
            if (!isPreviousBlockValid(nextBlock, currentMainBlock, isStraight)) {
                return false
            }
            currentMainBlock = nextBlock
        }
        return true
    }

    private fun isPreviousBlockValid(nextBlock: Block, currentBlock: Block, isStraight: Boolean): Boolean {
        return if (isStraight) {
            mainBlockService.isPreviousBlockValid(currentBlock, nextBlock)
        } else {
            mainBlockService.isPreviousBlockValid(nextBlock, currentBlock)
        }
    }

}