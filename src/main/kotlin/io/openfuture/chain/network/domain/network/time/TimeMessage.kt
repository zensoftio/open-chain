package io.openfuture.chain.network.domain

import io.netty.buffer.ByteBuf
import io.openfuture.chain.core.annotation.NoArgConstructor
import io.openfuture.chain.network.domain.base.BaseMessage

@NoArgConstructor
data class TimeMessage(
    var nodeTimestamp: Long,
    var networkTimestamp: Long
) : BaseMessage {

    override fun read(buffer: ByteBuf) {
        nodeTimestamp = buffer.readLong()
        networkTimestamp = buffer.readLong()
    }

    override fun write(buffer: ByteBuf) {
        buffer.writeLong(nodeTimestamp)
        buffer.writeLong(networkTimestamp)
    }

}
