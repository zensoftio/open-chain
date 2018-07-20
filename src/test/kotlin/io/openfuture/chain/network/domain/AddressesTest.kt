package io.openfuture.chain.network.domain

import io.netty.buffer.ByteBufUtil
import io.netty.buffer.Unpooled
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class AddressesTest {

    private val buffer = Unpooled.buffer().writeBytes(ByteBufUtil.decodeHexDump(
        "000000000002000000093132372e302e302e3100002382000000093132372e302e302e3100002383"))
    private val entity = Addresses(listOf(
        NetworkAddress("127.0.0.1", 9090),
        NetworkAddress("127.0.0.1", 9091)))

    @Test
    fun sendShouldWriteExactValuesInBuffer() {
        val actualBuffer = Unpooled.buffer()
        entity.write(actualBuffer)
        assertThat(actualBuffer).isEqualTo(buffer)
    }

    @Test
    fun getShouldFillEntityWithExactValuesFromBuffer() {
        val actualEntity = Addresses::class.java.newInstance()
        actualEntity.read(buffer)
        assertThat(actualEntity).isEqualTo(entity)
    }

}