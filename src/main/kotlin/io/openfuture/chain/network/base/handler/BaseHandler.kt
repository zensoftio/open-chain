package io.openfuture.chain.network.base.handler

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.util.ReferenceCountUtil
import io.openfuture.chain.network.domain.Packet

abstract class BaseHandler<T : Packet>(
        private val autoRelease: Boolean = true
) : ChannelInboundHandlerAdapter() {

    private val genericClass = this::class.supertypes[0].arguments[0].type!!.classifier!!


    final override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
        var release = true
        try {
            if (msg::class == genericClass) {
                channelRead(ctx, msg as T)
            } else {
                release = false
                ctx.fireChannelRead(msg)
            }
        } finally {
            if (autoRelease && release) {
                ReferenceCountUtil.release(msg)
            }
        }
    }

    abstract fun channelRead(ctx: ChannelHandlerContext, message: T)

}