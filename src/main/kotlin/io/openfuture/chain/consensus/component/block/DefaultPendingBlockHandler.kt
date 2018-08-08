package io.openfuture.chain.consensus.component.block

import io.openfuture.chain.consensus.component.block.BlockApprovalStage.*
import io.openfuture.chain.consensus.service.EpochService
import io.openfuture.chain.core.component.NodeKeyHolder
import io.openfuture.chain.core.model.entity.Delegate
import io.openfuture.chain.core.service.MainBlockService
import io.openfuture.chain.core.util.DictionaryUtils
import io.openfuture.chain.crypto.util.SignatureUtils
import io.openfuture.chain.network.message.consensus.BlockApprovalMessage
import io.openfuture.chain.network.message.consensus.PendingBlockMessage
import io.openfuture.chain.network.service.NetworkApiService
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils
import org.springframework.stereotype.Component

@Component
class DefaultPendingBlockHandler(
    private val epochService: EpochService,
    private val mainBlockService: MainBlockService,
    private val keyHolder: NodeKeyHolder,
    private val networkService: NetworkApiService
) : PendingBlockHandler {

    private val pendingBlocks: MutableSet<PendingBlockMessage> = mutableSetOf()
    private val prepareVotes: MutableMap<String, Delegate> = mutableMapOf()
    private val commits: MutableMap<String, MutableList<Delegate>> = mutableMapOf()

    private var observable: PendingBlockMessage? = null
    private var timeSlotNumber: Long = 0
    private var stage: BlockApprovalStage = IDLE


    @Synchronized
    override fun addBlock(block: PendingBlockMessage) {
        val blockSlotNumber = epochService.getSlotNumber(block.timestamp)
        if (blockSlotNumber > timeSlotNumber || epochService.isInIntermission(block.timestamp)) {
            this.reset()
        }

        pendingBlocks.add(block)
        val slotOwner = epochService.getCurrentSlotOwner()
        if (slotOwner.publicKey == block.publicKey && mainBlockService.isValid(block)) {
            networkService.broadcast(block)
            if (IDLE == stage && isActiveDelegate()) {
                this.observable = block
                this.stage = PREPARE
                this.timeSlotNumber = blockSlotNumber
                val vote = BlockApprovalMessage(PREPARE.getId(), block.hash, keyHolder.getPublicKey())
                vote.signature = SignatureUtils.sign(vote.getBytes(), keyHolder.getPrivateKey())
                networkService.broadcast(vote)
            }
        }
    }

    @Synchronized
    override fun handleApproveMessage(message: BlockApprovalMessage) {
        when (DictionaryUtils.valueOf(BlockApprovalStage::class.java, message.stageId)) {
            PREPARE -> handlePrevote(message)
            COMMIT -> handleCommit(message)
            IDLE -> throw IllegalArgumentException("Unacceptable message type")
        }
    }

    private fun handlePrevote(message: BlockApprovalMessage) {
        val delegates = epochService.getDelegates()
        val delegate = delegates.find { message.publicKey == it.publicKey }

        if (null == delegate || message.hash != observable!!.hash || !isActiveDelegate()) {
            return
        }

        if (!prepareVotes.containsKey(message.publicKey) && isValidApprovalSignature(message)) {
            prepareVotes[message.publicKey] = delegate
            networkService.broadcast(message)
            if (prepareVotes.size > (delegates.size - 1) / 3) {
                this.stage = COMMIT
                val commit = BlockApprovalMessage(COMMIT.getId(), message.hash, keyHolder.getPublicKey())
                commit.signature = SignatureUtils.sign(message.getBytes(), keyHolder.getPrivateKey())
                networkService.broadcast(commit)
            }
        }
    }

    private fun handleCommit(message: BlockApprovalMessage) {
        val delegates = epochService.getDelegates()
        val delegate = delegates.find { message.publicKey == it.publicKey } ?: return

        val blockCommits = commits[message.hash]
        if (null != blockCommits) {
            if (!blockCommits.contains(delegate) && isValidApprovalSignature(message)) {
                blockCommits.add(delegate)
                networkService.broadcast(message)
                if (blockCommits.size > (delegates.size - 1) / 3 * 2) {
                    pendingBlocks.find { it.hash == message.hash }?.let { mainBlockService.add(it) }
                }
            }
        } else {
            commits[message.hash] = mutableListOf(delegate)
        }
    }

    private fun reset() {
        this.stage = IDLE
        prepareVotes.clear()
        commits.clear()
        pendingBlocks.clear()
    }

    private fun isValidApprovalSignature(message: BlockApprovalMessage): Boolean =
        SignatureUtils.verify(message.getBytes(), message.signature!!, ByteUtils.fromHexString(message.publicKey))

    private fun isActiveDelegate(): Boolean =
        epochService.getDelegates().any { it.publicKey == keyHolder.getPublicKey() }

}