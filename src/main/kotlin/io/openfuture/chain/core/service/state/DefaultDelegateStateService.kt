package io.openfuture.chain.core.service.state

import io.openfuture.chain.consensus.property.ConsensusProperties
import io.openfuture.chain.core.component.StatePool
import io.openfuture.chain.core.model.entity.state.DelegateState
import io.openfuture.chain.core.repository.DelegateStateRepository
import io.openfuture.chain.core.service.DelegateStateService
import io.openfuture.chain.network.message.core.DelegateStateMessage
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class DefaultDelegateStateService(
    private val repository: DelegateStateRepository,
    private val consensusProperties: ConsensusProperties,
    private val statePool: StatePool
) : BaseStateService<DelegateState>(repository), DelegateStateService {

    companion object {
        const val DEFAULT_DELEGATE_RATING = 0L
    }


    override fun getAllDelegates(): List<DelegateState> = repository.findLastAll()

    override fun getActiveDelegates(): List<DelegateState> =
        getAllDelegates().sortedBy { it.rating }.take(consensusProperties.delegatesCount!!)

    override fun addDelegate(publicKey: String) {
        statePool.update(DelegateStateMessage(publicKey, DEFAULT_DELEGATE_RATING))
    }

}