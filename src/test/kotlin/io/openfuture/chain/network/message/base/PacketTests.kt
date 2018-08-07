package io.openfuture.chain.network.message.base

import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.openfuture.chain.config.MessageTests
import io.openfuture.chain.network.message.network.FindAddressesMessage
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class PacketTests : MessageTests() {

    private lateinit var message: Packet
    private lateinit var buffer: ByteBuf


    @Before
    fun setup() {
        buffer = createBuffer("00000005312e302e30000000000000007b02")
        message = Packet(FindAddressesMessage(), "1.0.0", 123)
    }

    @Test
    fun writeShouldWriteExactValuesInBuffer() {
        val actualBuffer = Unpooled.buffer()

        message.write(actualBuffer)

        assertThat(actualBuffer).isEqualTo(buffer)
    }

    @Test
    fun readShouldFillEntityWithExactValuesFromBuffer() {
        val actualMessage = Packet::class.java.newInstance()

        actualMessage.read(buffer)

        assertThat(actualMessage).isEqualToComparingFieldByFieldRecursively(message)
    }

}