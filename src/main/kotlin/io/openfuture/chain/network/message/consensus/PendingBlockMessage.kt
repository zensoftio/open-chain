package io.openfuture.chain.network.message.consensus

import io.netty.buffer.ByteBuf
import io.openfuture.chain.core.annotation.NoArgConstructor
import io.openfuture.chain.network.extension.readList
import io.openfuture.chain.network.extension.readString
import io.openfuture.chain.network.extension.writeList
import io.openfuture.chain.network.extension.writeString
import io.openfuture.chain.network.message.core.*

@NoArgConstructor
class PendingBlockMessage(
    height: Long,
    previousHash: String,
    timestamp: Long,
    reward: Long,
    hash: String,
    signature: String,
    publicKey: String,
    var merkleHash: String,
    var voteTransactions: List<VoteTransactionMessage>,
    var delegateTransactions: List<DelegateTransactionMessage>,
    var transferTransactions: List<TransferTransactionMessage>
) : BlockMessage(height, previousHash, timestamp, reward, hash, signature, publicKey) {

    fun getAllTransactions(): List<TransactionMessage> {
        return voteTransactions + delegateTransactions + transferTransactions
    }

    override fun read(buffer: ByteBuf) {
        super.read(buffer)

        merkleHash = buffer.readString()
        voteTransactions = buffer.readList()
        delegateTransactions = buffer.readList()
        transferTransactions = buffer.readList()
    }

    override fun write(buffer: ByteBuf) {
        super.write(buffer)

        buffer.writeString(merkleHash)
        buffer.writeList(voteTransactions)
        buffer.writeList(delegateTransactions)
        buffer.writeList(transferTransactions)
    }

    override fun toString() = "PendingBlockMessage(hash=$hash)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PendingBlockMessage

        if (hash != other.hash) return false
        if (timestamp != other.timestamp) return false
        if (signature != other.signature) return false
        if (publicKey != other.publicKey) return false
        if (height != other.height) return false
        if (previousHash != other.previousHash) return false
        if (reward != other.reward) return false
        if (merkleHash != other.merkleHash) return false
        if (voteTransactions != other.voteTransactions) return false
        if (delegateTransactions != other.delegateTransactions) return false
        if (transferTransactions != other.transferTransactions) return false

        return true
    }

    override fun hashCode(): Int {
        var result = hash.hashCode()
        result = 31 * result + timestamp.hashCode()
        result = 31 * result + signature.hashCode()
        result = 31 * result + publicKey.hashCode()
        result = 31 * result + height.hashCode()
        result = 31 * result + previousHash.hashCode()
        result = 31 * result + reward.hashCode()
        result = 31 * result + merkleHash.hashCode()
        result = 31 * result + voteTransactions.hashCode()
        result = 31 * result + delegateTransactions.hashCode()
        result = 31 * result + transferTransactions.hashCode()
        return result
    }

}