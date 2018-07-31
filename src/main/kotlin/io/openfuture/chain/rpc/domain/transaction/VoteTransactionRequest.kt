package io.openfuture.chain.rpc.domain.transaction

import io.openfuture.chain.consensus.model.dto.transaction.data.VoteTransactionData
import io.openfuture.chain.consensus.model.entity.transaction.unconfirmed.UVoteTransaction
import io.openfuture.chain.consensus.util.TransactionUtils

class VoteTransactionRequest(
    data: VoteTransactionData
) : BaseTransactionRequest<UVoteTransaction, VoteTransactionData>(data) {

    override fun toEntity(timestamp: Long): UVoteTransaction = UVoteTransaction(
        timestamp,
        data!!.amount,
        data!!.fee,
        data!!.recipientAddress,
        data!!.senderAddress,
        senderPublicKey!!,
        senderSignature!!,
        TransactionUtils.createHash(data!!, senderPublicKey!!, senderSignature!!),
        data!!.voteTypeId,
        data!!.delegateKey
    )

}