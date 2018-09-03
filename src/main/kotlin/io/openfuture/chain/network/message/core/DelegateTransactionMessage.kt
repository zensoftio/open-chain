package io.openfuture.chain.network.message.core

import io.netty.buffer.ByteBuf
import io.openfuture.chain.core.annotation.NoArgConstructor
import io.openfuture.chain.network.extension.readString
import io.openfuture.chain.network.extension.writeString

@NoArgConstructor
class DelegateTransactionMessage(
    timestamp: Long,
    fee: Long,
    senderAddress: String,
    hash: String,
    senderSignature: String,
    senderPublicKey: String,
    var nodeId: String,
    var delegateKey: String,
    var delegateHost: String,
    var delegatePort: Int,
    var amount: Long
) : TransactionMessage(timestamp, fee, senderAddress, hash, senderSignature, senderPublicKey) {

    override fun read(buffer: ByteBuf) {
        super.read(buffer)
        nodeId = buffer.readString()
        delegateKey = buffer.readString()
        delegateHost = buffer.readString()
        delegatePort = buffer.readInt()
        amount = buffer.readLong()
    }

    override fun write(buffer: ByteBuf) {
        super.write(buffer)
        buffer.writeString(nodeId)
        buffer.writeString(delegateKey)
        buffer.writeString(delegateHost)
        buffer.writeInt(delegatePort)
        buffer.writeLong(amount)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DelegateTransactionMessage) return false
        if (!super.equals(other)) return false

        if (nodeId != other.nodeId) return false
        if (delegateKey != other.delegateKey) return false
        if (delegateHost != other.delegateHost) return false
        if (delegatePort != other.delegatePort) return false
        if (amount != other.amount) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + nodeId.hashCode()
        result = 31 * result + delegateKey.hashCode()
        result = 31 * result + delegateHost.hashCode()
        result = 31 * result + delegatePort
        result = 31 * result + amount.hashCode()
        return result
    }


}