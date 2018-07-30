package io.openfuture.chain.service.block

import io.openfuture.chain.entity.block.Block
import io.openfuture.chain.exception.NotFoundException
import io.openfuture.chain.repository.BlockRepository
import io.openfuture.chain.service.CommonBlockService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DefaultCommonBlockService(
    private val repository: BlockRepository<Block>
) : CommonBlockService {

    @Transactional(readOnly = true)
    override fun getLast(): Block = repository.findFirstByOrderByHeightDesc()
        ?: throw NotFoundException("Not found last block")

    @Transactional(readOnly = true)
    override fun get(hash: String): Block = repository.findByHash(hash)
        ?: throw NotFoundException("Not found block with such hash: $hash")

    @Transactional(readOnly = true)
    override fun getBlocksAfterCurrentHash(hash: String): List<Block>? {
        val block = repository.findByHash(hash)

        return block?.let { repository.findByHeightGreaterThan(block.height) }
    }

    @Transactional(readOnly = true)
    override fun isExists(hash: String): Boolean = repository.existsByHash(hash)

}