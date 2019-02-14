package io.openfuture.chain.rpc.domain.transaction.response

import io.openfuture.chain.core.model.entity.transaction.confirmed.TransferTransaction
import io.openfuture.chain.core.model.entity.transaction.unconfirmed.UnconfirmedTransferTransaction

class TransferTransactionResponse(
    timestamp: Long,
    fee: Long,
    senderAddress: String,
    senderSignature: String,
    senderPublicKey: String,
    hash: String,
    val amount: Long,
    val recipientAddress: String? = null,
    val data: String? = null,
    blockHash: String? = null
) : BaseTransactionResponse(timestamp, fee, senderAddress, senderSignature, senderPublicKey, hash, blockHash) {

    constructor(tx: UnconfirmedTransferTransaction) : this(
        tx.timestamp,
        tx.fee,
        tx.senderAddress,
        tx.signature,
        tx.publicKey,
        tx.hash,
        tx.getPayload().amount,
        tx.getPayload().recipientAddress,
        tx.getPayload().data
    )

    constructor(tx: TransferTransaction) : this(
        tx.timestamp,
        tx.fee,
        tx.senderAddress,
        tx.signature,
        tx.publicKey,
        tx.hash,
        tx.getPayload().amount,
        tx.getPayload().recipientAddress,
        tx.getPayload().data,
        tx.block?.hash
    )

}