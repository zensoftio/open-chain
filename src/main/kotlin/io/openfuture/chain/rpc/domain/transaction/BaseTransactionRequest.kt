package io.openfuture.chain.rpc.domain.transaction

import io.openfuture.chain.core.model.dto.transaction.data.BaseTransactionData
import io.openfuture.chain.core.model.entity.transaction.unconfirmed.UTransaction
import javax.validation.Valid
import javax.validation.constraints.NotBlank

abstract class BaseTransactionRequest<UEntity: UTransaction, Data : BaseTransactionData>(
    @field:Valid var data: Data? = null,
    @field:NotBlank var senderPublicKey: String? = null,
    @field:NotBlank var senderSignature: String? = null
) {

    abstract fun toEntity(timestamp: Long) : UEntity

}