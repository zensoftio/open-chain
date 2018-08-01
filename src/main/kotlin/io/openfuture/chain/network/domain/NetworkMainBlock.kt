package io.openfuture.chain.network.domain

import io.netty.buffer.ByteBuf
import io.openfuture.chain.consensus.annotation.NoArgConstructor
import io.openfuture.chain.core.model.dto.transaction.DelegateTransactionDto
import io.openfuture.chain.core.model.dto.transaction.TransferTransactionDto
import io.openfuture.chain.core.model.dto.transaction.VoteTransactionDto
import io.openfuture.chain.core.model.entity.block.MainBlock
import io.openfuture.chain.consensus.model.entity.transaction.DelegateTransaction
import io.openfuture.chain.consensus.model.entity.transaction.TransferTransaction
import io.openfuture.chain.entity.transaction.VoteTransaction
import io.openfuture.chain.network.extension.readList
import io.openfuture.chain.network.extension.readString
import io.openfuture.chain.network.extension.writeList
import io.openfuture.chain.network.extension.writeString

@NoArgConstructor
class NetworkMainBlock(
    height: Long,
    previousHash: String,
    blockTimestamp: Long,
    reward: Long,
    hash: String,
    signature: String,
    publicKey: String,
    var merkleHash: String,
    var transferTransactions: MutableList<TransferTransactionDto>,
    var voteTransactions: MutableList<VoteTransactionDto>,
    var delegateTransactions: MutableList<DelegateTransactionDto>
) : NetworkBlock(height, previousHash, blockTimestamp, reward, publicKey, hash, signature) {

    constructor(block: MainBlock) : this(
        block.height,
        block.previousHash,
        block.timestamp,
        block.reward,
        block.hash,
        block.signature!!,
        block.publicKey,
        block.merkleHash,
        block.transactions.filterIsInstance(TransferTransaction::class.java).map { TransferTransactionDto(it) }.toMutableList(),
        block.transactions.filterIsInstance(VoteTransaction::class.java).map { VoteTransactionDto(it) }.toMutableList(),
        block.transactions.filterIsInstance(DelegateTransaction::class.java).map { DelegateTransactionDto(it) }.toMutableList()
    )

    override fun readParams(buffer: ByteBuf) {
        super.readParams(buffer)

        merkleHash = buffer.readString()
        transferTransactions = buffer.readList()
        voteTransactions = buffer.readList()
        delegateTransactions = buffer.readList()
    }

    override fun writeParams(buffer: ByteBuf) {
        super.writeParams(buffer)

        buffer.writeString(merkleHash)
        buffer.writeList(transferTransactions)
        buffer.writeList(voteTransactions)
        buffer.writeList(delegateTransactions)
    }

    fun toEntity(): MainBlock = MainBlock(
        height,
        previousHash,
        blockTimestamp,
        reward,
        publicKey,
        merkleHash,
        mutableSetOf()).apply { signature = super.signature }


    override fun toString() = "NetworkMainBlock(hash=$hash)"

}