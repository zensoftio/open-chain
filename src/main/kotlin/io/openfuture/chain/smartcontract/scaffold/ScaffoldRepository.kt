package io.openfuture.chain.smartcontract.scaffold

import io.openfuture.chain.core.repository.BaseRepository
import org.springframework.stereotype.Repository

@Repository
interface ScaffoldRepository : BaseRepository<OpenScaffold> {

    fun findByRecipientAddress(recipientAddress: String): OpenScaffold?

}