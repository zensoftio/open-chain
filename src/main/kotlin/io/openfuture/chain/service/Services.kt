package io.openfuture.chain.service

import io.netty.channel.Channel
import io.openfuture.chain.crypto.domain.ECKey
import io.openfuture.chain.crypto.domain.ExtendedKey
import io.openfuture.chain.domain.base.PageRequest
import io.openfuture.chain.domain.rpc.HardwareInfo
import io.openfuture.chain.domain.rpc.crypto.AccountDto
import io.openfuture.chain.domain.rpc.hardware.CpuInfo
import io.openfuture.chain.domain.rpc.hardware.NetworkInfo
import io.openfuture.chain.domain.rpc.hardware.RamInfo
import io.openfuture.chain.domain.rpc.hardware.StorageInfo
import io.openfuture.chain.domain.rpc.transaction.BaseTransactionRequest
import io.openfuture.chain.domain.rpc.transaction.DelegateTransactionRequest
import io.openfuture.chain.domain.rpc.transaction.TransferTransactionRequest
import io.openfuture.chain.domain.rpc.transaction.VoteTransactionRequest
import io.openfuture.chain.domain.transaction.BaseTransactionDto
import io.openfuture.chain.domain.transaction.DelegateTransactionDto
import io.openfuture.chain.domain.transaction.TransferTransactionDto
import io.openfuture.chain.domain.transaction.VoteTransactionDto
import io.openfuture.chain.domain.transaction.data.BaseTransactionData
import io.openfuture.chain.domain.transaction.data.DelegateTransactionData
import io.openfuture.chain.domain.transaction.data.TransferTransactionData
import io.openfuture.chain.domain.transaction.data.VoteTransactionData
import io.openfuture.chain.entity.Delegate
import io.openfuture.chain.entity.Wallet
import io.openfuture.chain.entity.block.Block
import io.openfuture.chain.entity.block.MainBlock
import io.openfuture.chain.entity.transaction.*
import io.openfuture.chain.entity.transaction.unconfirmed.UDelegateTransaction
import io.openfuture.chain.entity.transaction.unconfirmed.UTransaction
import io.openfuture.chain.entity.transaction.unconfirmed.UTransferTransaction
import io.openfuture.chain.entity.transaction.unconfirmed.UVoteTransaction
import io.openfuture.chain.network.domain.NetworkAddress
import io.openfuture.chain.network.domain.Packet
import org.springframework.data.domain.Page

interface HardwareInfoService {

    fun getHardwareInfo(): HardwareInfo

    fun getCpuInfo(): CpuInfo

    fun getRamInfo(): RamInfo

    fun getDiskStorageInfo(): List<StorageInfo>

    fun getNetworksInfo(): List<NetworkInfo>

}

interface BlockService<T : Block> {

    fun get(hash: String): T

    fun getLast(): T

    fun save(block: T): T

    fun isValid(block: T): Boolean

}

interface CryptoService {

    fun generateSeedPhrase(): String

    fun generateNewAccount(): AccountDto

    fun getRootAccount(seedPhrase: String): AccountDto

    fun getDerivationKey(seedPhrase: String, derivationPath: String): ExtendedKey

    fun importKey(key: String): ExtendedKey

    fun importWifKey(wifKey: String): ECKey

    fun serializePublicKey(key: ExtendedKey): String

    fun serializePrivateKey(key: ExtendedKey): String

}

/**
 * The utility service that is not aware of transaction types, has default implementation
 */
interface BaseUTransactionService {

    fun getPending(): MutableSet<UTransaction>

}

interface BaseTransactionService<Entity : Transaction> {

    fun get(hash: String): Entity

    fun toBlock(tx: Entity, block: MainBlock): Entity

}

interface TransactionService<Entity : Transaction, UEntity : UTransaction> : BaseTransactionService<Entity>

interface RewardTransactionService : BaseTransactionService<RewardTransaction>

interface TransferTransactionService : TransactionService<TransferTransaction, UTransferTransaction>

interface VoteTransactionService : TransactionService<VoteTransaction, UVoteTransaction>

interface DelegateTransactionService : TransactionService<DelegateTransaction, UDelegateTransaction>

interface UTransactionService<Entity : UTransaction, Data : BaseTransactionData, Dto : BaseTransactionDto<Entity, Data>,
    Req : BaseTransactionRequest<Entity, Data>> {

    fun get(hash: String): Entity

    fun getAll(): MutableSet<Entity>

    fun add(dto: Dto): Entity

    fun add(request: Req): Entity

}

interface UTransferTransactionService : UTransactionService<UTransferTransaction, TransferTransactionData,
    TransferTransactionDto, TransferTransactionRequest>

interface UVoteTransactionService : UTransactionService<UVoteTransaction, VoteTransactionData,
    VoteTransactionDto, VoteTransactionRequest>

interface UDelegateTransactionService : UTransactionService<UDelegateTransaction, DelegateTransactionData,
    DelegateTransactionDto, DelegateTransactionRequest>

interface DelegateService {

    fun getAll(request: PageRequest): Page<Delegate>

    fun getByPublicKey(key: String): Delegate

    fun getActiveDelegates(): Set<Delegate>

    fun save(delegate: Delegate): Delegate

}

interface ConsensusService {

    fun getCurrentEpochHeight(): Long

    fun isGenesisBlockNeeded(): Boolean

}

interface WalletService {

    fun getByAddress(address: String): Wallet

    fun getBalanceByAddress(address: String): Long

    fun getVotesByAddress(address: String): MutableSet<Delegate>

    fun save(wallet: Wallet)

    fun updateBalance(from: String, to: String, amount: Long, fee: Long)

}

interface NetworkService {

    fun broadcast(packet: Packet)

    fun maintainConnectionNumber()

    fun connect(peers: List<NetworkAddress>)

}

interface ConnectionService {

    fun addConnection(channel: Channel, networkAddress: NetworkAddress)

    fun removeConnection(channel: Channel): NetworkAddress?

    fun getConnectionAddresses(): Set<NetworkAddress>

    fun getConnections(): MutableMap<Channel, NetworkAddress>

}