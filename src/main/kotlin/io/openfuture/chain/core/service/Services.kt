package io.openfuture.chain.core.service

import io.openfuture.chain.core.model.entity.Contract
import io.openfuture.chain.core.model.entity.Receipt
import io.openfuture.chain.core.model.entity.block.Block
import io.openfuture.chain.core.model.entity.block.GenesisBlock
import io.openfuture.chain.core.model.entity.block.MainBlock
import io.openfuture.chain.core.model.entity.dictionary.VoteType
import io.openfuture.chain.core.model.entity.state.AccountState
import io.openfuture.chain.core.model.entity.state.DelegateState
import io.openfuture.chain.core.model.entity.state.State
import io.openfuture.chain.core.model.entity.transaction.confirmed.*
import io.openfuture.chain.core.model.entity.transaction.unconfirmed.UnconfirmedDelegateTransaction
import io.openfuture.chain.core.model.entity.transaction.unconfirmed.UnconfirmedTransaction
import io.openfuture.chain.core.model.entity.transaction.unconfirmed.UnconfirmedTransferTransaction
import io.openfuture.chain.core.model.entity.transaction.unconfirmed.UnconfirmedVoteTransaction
import io.openfuture.chain.core.model.node.*
import io.openfuture.chain.rpc.domain.base.PageRequest
import org.springframework.data.domain.Page

interface HardwareInfoService {

    fun getHardwareInfo(): HardwareInfo

    fun getCpuInfo(): CpuInfo

    fun getRamInfo(): RamInfo

    fun getDiskStorageInfo(): List<StorageInfo>

    fun getNetworksInfo(): List<NetworkInfo>

}

interface BlockManager {

    fun getCount(): Long

    fun getLast(): Block

    fun findByHash(hash: String): Block?

    fun getAvgProductionTime(): Long

    fun getAllByHeightIn(heights: List<Long>): List<Block>

    fun getGenesisBlockByHash(hash: String): GenesisBlock

    fun getMainBlockByHash(hash: String): MainBlock

    fun getAllGenesisBlocks(request: PageRequest): Page<GenesisBlock>

    fun getAllMainBlocks(request: PageRequest): Page<MainBlock>

    fun getPreviousGenesisBlock(hash: String): GenesisBlock

    fun getPreviousMainBlock(hash: String): MainBlock

    fun getNextGenesisBlock(hash: String): GenesisBlock

    fun getNextMainBlock(hash: String): MainBlock

    fun getPreviousGenesisBlockByHeight(height: Long): GenesisBlock

    fun getLastGenesisBlock(): GenesisBlock

    fun findGenesisBlockByEpochIndex(epochIndex: Long): GenesisBlock?

    fun isGenesisBlockRequired(): Boolean

    fun getMainBlocksByEpochIndex(epochIndex: Long): List<MainBlock>

    fun createGenesisBlock(): GenesisBlock

    fun createMainBlock(): MainBlock

    fun add(block: Block)

    fun deleteByHeightIn(heights: List<Long>)

    fun removeEpoch(genesisBlock: GenesisBlock)

    fun verify(block: Block, lastBlock: Block, new: Boolean = true): Boolean
}

interface BlockService<T : Block> {

    fun getByHash(hash: String): T

    fun getAll(request: PageRequest): Page<T>

    fun getPreviousBlock(hash: String): T

    fun getNextBlock(hash: String): T

    fun create(): T

    fun add(block: T)

}

interface GenesisBlockService : BlockService<GenesisBlock> {

    fun getPreviousByHeight(height: Long): GenesisBlock

    fun getLast(): GenesisBlock

    fun findByEpochIndex(epochIndex: Long): GenesisBlock?

    fun isGenesisBlockRequired(): Boolean

}

interface MainBlockService : BlockService<MainBlock> {

    fun getBlocksByEpochIndex(epochIndex: Long): List<MainBlock>

}

interface BlockValidatorManager {

    fun verify(block: Block, lastBlock: Block, new: Boolean): Boolean

}

interface MainBlockValidator {

    fun validate(block: MainBlock, new: Boolean)

}

interface TransactionManager {

    fun getCount(): Long

    fun getCountDelegateTransactionsByBlock(block: Block): Long

    fun getCountTransferTransactionsByBlock(block: Block): Long

    fun getCountVoteTransactionsByBlock(block: Block): Long

    fun getUnconfirmedBalanceBySenderAddress(address: String): Long

    fun getProducingPerSecond(): Long

    fun getAllUnconfirmedDelegateTransactions(request: PageRequest): List<UnconfirmedDelegateTransaction>

    fun getAllUnconfirmedTransferTransactions(request: PageRequest): List<UnconfirmedTransferTransaction>

    fun getAllUnconfirmedVoteTransactions(request: PageRequest): List<UnconfirmedVoteTransaction>

    fun getAllTransferTransactions(request: PageRequest): Page<TransferTransaction>

    fun getAllTransferTransactionsByAddress(address: String, request: PageRequest): Page<TransferTransaction>

    fun getAllRewardTransactions(request: PageRequest): Page<RewardTransaction>

    fun getAllDelegateTransactionsByBlock(block: Block): List<DelegateTransaction>

    fun getAllTransferTransactionsByBlock(block: Block): List<TransferTransaction>

    fun getAllVoteTransactionsByBlock(block: Block): List<VoteTransaction>

    fun getDelegateTransactionByHash(hash: String): DelegateTransaction

    fun getTransferTransactionByHash(hash: String): TransferTransaction

    fun getVoteTransactionByHash(hash: String): VoteTransaction

    fun getRewardTransactionByBlock(block: Block): RewardTransaction?

    fun getRewardTransactionByRecipientAddress(address: String): List<RewardTransaction>

    fun getUnconfirmedVoteBySenderAgainstDelegate(senderAddress: String, delegateKey: String): UnconfirmedVoteTransaction?

    fun getLastVoteForDelegate(senderAddress: String, delegateKey: String): VoteTransaction

    fun createRewardTransaction(timestamp: Long, fees: Long): RewardTransaction

    fun <T : Transaction> commit(tx: T, receipt: Receipt): T

    fun <uT : UnconfirmedTransaction> add(uTx: uT): uT

    fun processTransactions(transactions: List<Transaction>, delegateWallet: String): List<Receipt>

    fun verify(tx: Transaction, new: Boolean = false): Boolean

    fun deleteBlockTransactions(blockHeights: List<Long>)

}

interface UTransactionService<uT : UnconfirmedTransaction> {

    fun getAll(): List<uT>

    fun getAll(request: PageRequest): List<uT>

    fun getAllBySenderAddress(address: String): List<uT>

    fun add(uTx: uT): uT

}

interface UDelegateTransactionService : UTransactionService<UnconfirmedDelegateTransaction>

interface UTransferTransactionService : UTransactionService<UnconfirmedTransferTransaction>

interface UVoteTransactionService : UTransactionService<UnconfirmedVoteTransaction> {

    fun getBySenderAgainstDelegate(senderAddress: String, delegateKey: String): UnconfirmedVoteTransaction?

}

interface TransactionService<T : Transaction> {

    fun getByHash(hash: String): T

    fun getAll(request: PageRequest): Page<T>

    fun commit(tx: T, receipt: Receipt): T

    fun process(tx: T, delegateWallet: String): Receipt

}

interface RewardTransactionService : TransactionService<RewardTransaction> {

    fun getByBlock(block: Block): RewardTransaction?

    fun getByRecipientAddress(address: String): List<RewardTransaction>

    fun create(timestamp: Long, fees: Long): RewardTransaction

}

interface ExternalTransactionService<T : Transaction> : TransactionService<T> {

    fun getCountByBlock(block: Block): Long

    fun getAllByBlock(block: Block): List<T>

}

interface TransferTransactionService : ExternalTransactionService<TransferTransaction> {

    fun getAllByAddress(address: String, request: PageRequest): Page<TransferTransaction>

}

interface VoteTransactionService : ExternalTransactionService<VoteTransaction> {

    fun getLastVoteForDelegate(senderAddress: String, delegateKey: String): VoteTransaction

}

interface DelegateTransactionService : ExternalTransactionService<DelegateTransaction>

interface TransactionValidatorManager {

    fun validate(tx: Transaction, new: Boolean = true)

    fun verify(tx: Transaction, new: Boolean): Boolean

}

interface TransactionValidator<T> {

    fun validate(tx: T, new: Boolean)

}

interface DelegateTransactionValidator : TransactionValidator<DelegateTransaction>

interface TransferTransactionValidator : TransactionValidator<TransferTransaction>

interface VoteTransactionValidator : TransactionValidator<VoteTransaction>

interface StateManager {

    fun <T : State> getLastByAddress(address: String): T

    fun getAllDelegateStatesByBlock(block: Block): List<DelegateState>

    fun getAllAccountStatesByBlock(block: Block): List<AccountState>

    fun getWalletBalanceByAddress(address: String): Long

    fun getVotesForDelegate(delegateKey: String): List<AccountState>

    fun updateWalletBalanceByAddress(address: String, amount: Long)

    fun updateVoteByAddress(address: String, delegateKey: String, voteType: VoteType)

    fun updateSmartContractStorage(address: String, storage: String)

    fun getAllDelegates(request: PageRequest): List<DelegateState>

    fun getActiveDelegates(): List<DelegateState>

    fun isExistsDelegateByPublicKey(key: String): Boolean

    fun addDelegate(delegateKey: String, walletAddress: String, createDate: Long)

    fun updateDelegateRating(delegateKey: String, amount: Long)

    fun commit(state: State)

    fun verify(state: State): Boolean

    fun deleteBlockStates(blockHeights: List<Long>)

}

interface StateService<T : State> {

    fun getAllByBlock(block: Block): List<T>

}

interface DelegateStateService : StateService<DelegateState> {

    fun getAllDelegates(request: PageRequest): List<DelegateState>

    fun getActiveDelegates(): List<DelegateState>

    fun isExistsByPublicKey(key: String): Boolean

    fun addDelegate(delegateKey: String, walletAddress: String, createDate: Long): DelegateState

    fun updateRating(delegateKey: String, amount: Long): DelegateState

}

interface AccountStateService : StateService<AccountState> {

    fun getBalanceByAddress(address: String): Long

    fun getVotesForDelegate(delegateKey: String): List<AccountState>

    fun updateBalanceByAddress(address: String, amount: Long): AccountState

    fun updateVoteByAddress(address: String, delegateKey: String?): AccountState

    fun updateStorage(address: String, storage: String): AccountState

}

interface StateValidatorManager {

    fun verify(state: State): Boolean

}

interface ContractService {

    fun getByAddress(address: String): Contract

    fun getAllByOwner(owner: String): List<Contract>

    fun save(contract: Contract): Contract

    fun generateAddress(owner: String): String
}

interface ReceiptService {

    fun getByTransactionHash(hash: String): Receipt

    fun getAllByBlock(block: Block): List<Receipt>

    fun commit(receipt: Receipt)

    fun verify(receipt: Receipt): Boolean

    fun deleteBlockReceipts(blockHeights: List<Long>)

}

interface ReceiptValidator {

    fun verify(receipt: Receipt): Boolean

}