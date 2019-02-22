package io.openfuture.chain.core.component

import io.openfuture.chain.core.model.entity.block.Block
import io.openfuture.chain.core.service.BlockManager
import io.openfuture.chain.core.service.block.validation.MainBlockValidator
import io.openfuture.chain.core.service.block.validation.pipeline.BlockValidationPipeline
import io.openfuture.chain.core.sync.SyncMode
import io.openfuture.chain.core.sync.SyncMode.FULL
import io.openfuture.chain.core.sync.SyncMode.LIGHT
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class DBChecker(
    private val blockManager: BlockManager,
    private val mainBlockValidator: MainBlockValidator
) {

    @Transactional
    fun prepareDB(syncMode: SyncMode) {
        val pipeline = when (syncMode) {
            FULL -> BlockValidationPipeline(mainBlockValidator.checkFull())
            LIGHT -> BlockValidationPipeline(mainBlockValidator.checkLight())
        }
        val lastBlock = blockManager.getLast()
        val lastValidBlockHeight = lastValidBlockHeight(pipeline)
        val failBlockHeight = lastValidBlockHeight + 1L

        if (failBlockHeight <= lastBlock.height) {
            val range = LongRange(failBlockHeight, lastBlock.height)
            blockManager.deleteByHeightIn(range.toList())
        }
    }

    private fun lastValidBlockHeight(pipeline: BlockValidationPipeline): Long {
        val lastEpochIndex = blockManager.getLastGenesisBlock().getPayload().epochIndex
        var lastValidBlockHeight = 1L
        loop@ for (epochIndex in 1L..lastEpochIndex) {
            val genesisBlock = blockManager.findGenesisBlockByEpochIndex(epochIndex)!!

            var previousBlock: Block = genesisBlock
            val blocks = blockManager.getMainBlocksByEpochIndex(epochIndex, FULL)

            for (index in 0 until blocks.size) {
                val block = blocks[index]
                if (!mainBlockValidator.verify(block, previousBlock, blockManager.getLastMainBlock(), false, pipeline)) {
                    break@loop
                }

                lastValidBlockHeight = block.height
                previousBlock = block
            }
        }

        return lastValidBlockHeight
    }

}