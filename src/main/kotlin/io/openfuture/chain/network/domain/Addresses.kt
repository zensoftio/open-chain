package io.openfuture.chain.network.domain

import io.netty.buffer.ByteBuf

class Addresses() : Packet() {

    val values : MutableList<NetworkAddress> = mutableListOf()

    constructor(values : List<NetworkAddress>) : this(){
        this.values.addAll(values)
    }

    override fun get(buffer: ByteBuf) {
        val size = buffer.readInt()
        values.clear()
        for (index in 1..size) {
            val address = NetworkAddress()
            address.get(buffer)
            values.add(address)
        }
    }

    override fun send(buffer: ByteBuf) {
        buffer.writeInt(values.size)
        for (address in values) {
            address.send(buffer)
        }
    }

    override fun toString() = "Addresses(values=$values)"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Addresses

        if (values != other.values) return false

        return true
    }

    override fun hashCode(): Int {
        return values.hashCode()
    }

}