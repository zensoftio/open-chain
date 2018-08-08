package io.openfuture.chain.core.repository

import io.openfuture.chain.core.model.entity.Delegate
import io.openfuture.chain.core.model.entity.Wallet
import io.openfuture.chain.core.model.entity.block.BaseBlock
import io.openfuture.chain.core.model.entity.block.GenesisBlock
import io.openfuture.chain.core.model.entity.block.MainBlock
import io.openfuture.chain.core.model.entity.transaction.confirmed.DelegateTransaction
import io.openfuture.chain.core.model.entity.transaction.confirmed.Transaction
import io.openfuture.chain.core.model.entity.transaction.confirmed.TransferTransaction
import io.openfuture.chain.core.model.entity.transaction.confirmed.VoteTransaction
import io.openfuture.chain.core.model.entity.transaction.unconfirmed.UnconfirmedDelegateTransaction
import io.openfuture.chain.core.model.entity.transaction.unconfirmed.UnconfirmedTransaction
import io.openfuture.chain.core.model.entity.transaction.unconfirmed.UnconfirmedTransferTransaction
import io.openfuture.chain.core.model.entity.transaction.unconfirmed.UnconfirmedVoteTransaction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.PagingAndSortingRepository
import org.springframework.stereotype.Repository

@NoRepositoryBean
interface BaseRepository<T> : JpaRepository<T, Int>, PagingAndSortingRepository<T, Int>

@Repository
interface BlockRepository<Entity: BaseBlock> : BaseRepository<Entity>{

    fun findOneByHash(hash: String): Entity?

    fun findFirstByOrderByHeightDesc(): Entity?

    fun findAllByHeightGreaterThan(height: Long): List<Entity>

}

@Repository
interface MainBlockRepository : BlockRepository<MainBlock>

@Repository
interface GenesisBlockRepository : BlockRepository<GenesisBlock>

@Repository
interface TransactionRepository<Entity : Transaction> : BaseRepository<Entity> {

    fun findOneByHash(hash: String): Entity?

}

@Repository
interface VoteTransactionRepository: TransactionRepository<VoteTransaction>

@Repository
interface DelegateTransactionRepository: TransactionRepository<DelegateTransaction>

@Repository
interface TransferTransactionRepository: TransactionRepository<TransferTransaction>

@Repository
interface UTransactionRepository<UEntity : UnconfirmedTransaction> : BaseRepository<UEntity> {

    fun findOneByHash(hash: String): UEntity?

    fun findAllByOrderByFeeDesc(): MutableList<UEntity>

    fun findAllBySenderAddress(address: String): List<UEntity>

}

@Repository
interface UVoteTransactionRepository: UTransactionRepository<UnconfirmedVoteTransaction>

@Repository
interface UDelegateTransactionRepository: UTransactionRepository<UnconfirmedDelegateTransaction>

@Repository
interface UTransferTransactionRepository: UTransactionRepository<UnconfirmedTransferTransaction>

@Repository
interface DelegateRepository : BaseRepository<Delegate> {

    fun findOneByPublicKey(key: String): Delegate?

}

@Repository
interface WalletRepository : BaseRepository<Wallet> {

    fun findOneByAddress(address: String): Wallet?

}