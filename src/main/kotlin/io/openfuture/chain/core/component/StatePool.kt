package io.openfuture.chain.core.component

import io.openfuture.chain.core.model.entity.State
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class StatePool {

    private val pool: MutableMap<String, State> = ConcurrentHashMap()


    fun getPool(): Map<String, State> = pool

    fun get(address: String): State? = pool[address]

    fun update(state: State): State? = pool.put(state.address, state)

    fun clear() {
        pool.clear()
    }

}