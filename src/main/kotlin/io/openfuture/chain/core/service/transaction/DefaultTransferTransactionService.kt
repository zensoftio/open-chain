package io.openfuture.chain.core.service.transaction

import io.openfuture.chain.core.exception.NotFoundException
import io.openfuture.chain.core.exception.ValidationException
import io.openfuture.chain.core.model.entity.block.MainBlock
import io.openfuture.chain.core.model.entity.transaction.TransactionHeader
import io.openfuture.chain.core.model.entity.transaction.confirmed.TransferTransaction
import io.openfuture.chain.core.model.entity.transaction.payload.TransferTransactionPayload
import io.openfuture.chain.core.model.entity.transaction.unconfirmed.UnconfirmedTransferTransaction
import io.openfuture.chain.core.repository.TransferTransactionRepository
import io.openfuture.chain.core.repository.UTransferTransactionRepository
import io.openfuture.chain.core.service.TransferTransactionService
import io.openfuture.chain.network.message.core.TransferTransactionMessage
import io.openfuture.chain.network.service.NetworkApiService
import io.openfuture.chain.rpc.domain.base.PageRequest
import io.openfuture.chain.rpc.domain.transaction.request.TransferTransactionRequest
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DefaultTransferTransactionService(
    repository: TransferTransactionRepository,
    uRepository: UTransferTransactionRepository,
    private val networkService: NetworkApiService
) : BaseTransactionService<TransferTransaction, UnconfirmedTransferTransaction>(repository, uRepository), TransferTransactionService {

    companion object {
        val log = LoggerFactory.getLogger(DefaultTransferTransactionService::class.java)
    }


    @Transactional(readOnly = true)
    override fun getAll(request: PageRequest): Page<TransferTransaction> = repository.findAll(request)

    @Transactional(readOnly = true)
    override fun getAllUnconfirmed(): MutableList<UnconfirmedTransferTransaction> = unconfirmedRepository.findAllByOrderByHeaderFeeDesc()

    @Transactional(readOnly = true)
    override fun getUnconfirmedByHash(hash: String): UnconfirmedTransferTransaction = unconfirmedRepository.findOneByHash(hash)
        ?: throw NotFoundException("Transaction with hash $hash not found")

    @Transactional(readOnly = true)
    override fun getByAddress(address: String): List<TransferTransaction> {
        val senderTransactions = repository.findAllByHeaderSenderAddress(address)
        val recipientTransactions = (repository as TransferTransactionRepository).findAllByPayloadRecipientAddress(address)

        return senderTransactions + recipientTransactions
    }

    @Transactional
    override fun add(message: TransferTransactionMessage): UnconfirmedTransferTransaction {
        val utx = UnconfirmedTransferTransaction.of(message)

        if (isExists(utx.hash)) {
            return utx
        }

        validate(utx)
        val savedUtx = this.save(utx)
        networkService.broadcast(message)
        return savedUtx
    }

    @Transactional
    override fun add(request: TransferTransactionRequest): UnconfirmedTransferTransaction {
        val utx = UnconfirmedTransferTransaction.of(request)

        if (isExists(utx.hash)) {
            return utx
        }

        validate(utx)
        val savedUtx = this.save(utx)
        networkService.broadcast(savedUtx.toMessage())
        return savedUtx
    }

    @Transactional
    override fun toBlock(message: TransferTransactionMessage, block: MainBlock): TransferTransaction {
        val tx = repository.findOneByHash(message.hash)
        if (null != tx) {
            return tx
        }

        val utx = unconfirmedRepository.findOneByHash(message.hash)
        if (null != utx) {
            return confirm(utx, TransferTransaction.of(utx, block))
        }

        return this.save(TransferTransaction.of(message, block))
    }

    @Transactional
    override fun save(tx: TransferTransaction): TransferTransaction {
        updateTransferBalance(tx.header.senderAddress, tx.payload.recipientAddress, tx.payload.amount)
        return super.save(tx)
    }

    @Transactional
    override fun isValid(message: TransferTransactionMessage): Boolean {
        return try {
            validate(UnconfirmedTransferTransaction.of(message))
            true
        } catch (e: ValidationException) {
            log.warn(e.message)
            false
        }
    }

    private fun validate(utx: UnconfirmedTransferTransaction) {
        validateLocal(utx.header, utx.payload)
        super.validateBase(utx)
    }

    private fun validateLocal(header: TransactionHeader, payload: TransferTransactionPayload) {
        if (!isValidBalance(header.senderAddress, payload.amount, header.fee)) {
            throw ValidationException("Invalid balance for transfer transaction")
        }
    }

    private fun updateTransferBalance(from: String, to: String, amount: Long) {
        walletService.increaseBalance(to, amount)
        walletService.decreaseBalance(from, amount)
    }

    private fun isValidBalance(address: String, amount: Long, fee: Long): Boolean {
        if (amount < 0 || fee < 0) {
            return false
        }

        val balance = walletService.getBalanceByAddress(address)
        val unconfirmedFee = baseService.getAllUnconfirmedByAddress(address).map { it.header.fee }.sum()
        val unconfirmedAmount = unconfirmedRepository.findAllByHeaderSenderAddress(address).map { it.payload.amount }.sum()
        val unspentBalance = balance - (unconfirmedFee + unconfirmedAmount)

        return unspentBalance >= amount + fee
    }

}