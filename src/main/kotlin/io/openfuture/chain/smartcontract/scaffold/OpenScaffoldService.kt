package io.openfuture.chain.smartcontract.scaffold

interface OpenScaffoldService {

    fun save(request: SaveOpenScaffoldRequest) : OpenScaffold

    fun findByRecipientAddress(addresses: String): OpenScaffold?
}