package io.openfuture.chain.smartcontract.scaffold

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DefaultOpenScaffoldService(
        private val repository: ScaffoldRepository
) : OpenScaffoldService {

    @Transactional
    override fun save(request: SaveOpenScaffoldRequest): OpenScaffold {
        repository.findByRecipientAddress(request.address)?.let {
            throw Exception("OpenScaffold already exists with an address ".plus(request.address))
        }

        return repository.save(OpenScaffold(request.address, request.webHook))
    }

    @Transactional(readOnly = true)
    override fun findByRecipientAddress(addresses: String): OpenScaffold? {
        return repository.findByRecipientAddress(addresses)
    }
}