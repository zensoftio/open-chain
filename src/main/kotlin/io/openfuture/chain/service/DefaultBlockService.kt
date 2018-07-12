package io.openfuture.chain.service

import io.openfuture.chain.entity.Block
import io.openfuture.chain.entity.GenesisBlock
import io.openfuture.chain.entity.MainBlock
import io.openfuture.chain.exception.NotFoundException
import io.openfuture.chain.repository.BlockRepository
import io.openfuture.chain.repository.GenesisBlockRepository
import io.openfuture.chain.repository.MainBlockRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DefaultBlockService(
    private val blockRepository: BlockRepository<Block>,
    private val mainBlockRepository: MainBlockRepository,
    private val genesisBlockRepository: GenesisBlockRepository,
    private val transactionService: TransactionService,
    private val walletService: WalletService
) : BlockService {

    @Transactional(readOnly = true)
    override fun get(hash: String): Block = blockRepository.findByHash(hash)
        ?: throw NotFoundException("Block with hash:$hash not found ")

    @Transactional(readOnly = true)
    override fun getLastMain():Block =
        mainBlockRepository.findFirstByOrderByHeightDesc()
            ?: throw NotFoundException("Last block not found!")

    @Transactional(readOnly = true)
    override fun getLastGenesis(): GenesisBlock =
        genesisBlockRepository.findFirstByOrderByHeightDesc()
            ?: throw NotFoundException("Last Genesis block not exist!")

    @Transactional
    override fun save(block: Block): Block {
        val savedBlock = blockRepository.save(block)
        if (block is MainBlock) {
            val transactions = block.transactions
            for (transaction in transactions) {
                transaction.blockHash = savedBlock.hash
                walletService.updateByTransaction(transaction)
            }
            transactionService.saveAll(transactions)
        }
        return savedBlock
    }

}