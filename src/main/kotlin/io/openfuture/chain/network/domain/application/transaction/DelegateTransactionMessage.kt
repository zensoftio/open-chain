package io.openfuture.chain.network.domain.application.transaction

import io.openfuture.chain.consensus.model.entity.transaction.DelegateTransaction
import io.openfuture.chain.core.annotation.NoArgConstructor
import io.openfuture.chain.core.model.entity.transaction.unconfirmed.UDelegateTransaction
import io.openfuture.chain.network.domain.application.transaction.data.DelegateTransactionData

@NoArgConstructor
class DelegateTransactionMessage(
    data: DelegateTransactionData,
    timestamp: Long,
    senderPublicKey: String,
    senderSignature: String,
    hash: String
) : BaseTransactionMessage<DelegateTransactionData>(data, timestamp, senderPublicKey, senderSignature, hash) {

    constructor(tx: DelegateTransaction) : this(
        DelegateTransactionData(tx.amount, tx.fee, tx.recipientAddress, tx.senderAddress, tx.delegateKey),
        tx.timestamp,
        tx.senderPublicKey,
        tx.senderSignature,
        tx.hash
    )

    constructor(tx: UDelegateTransaction) : this(
        DelegateTransactionData(tx.amount, tx.fee, tx.recipientAddress, tx.senderAddress, tx.delegateKey),
        tx.timestamp,
        tx.senderPublicKey,
        tx.senderSignature,
        tx.hash
    )

    fun toEntity(): DelegateTransaction = DelegateTransaction(
        timestamp,
        data.amount,
        data.fee,
        data.recipientAddress,
        data.senderAddress,
        senderPublicKey,
        senderSignature,
        hash,
        data.delegateKey
    )

    fun toUEntity(): UDelegateTransaction = UDelegateTransaction(
        timestamp,
        data.amount,
        data.fee,
        data.recipientAddress,
        data.senderAddress,
        senderPublicKey,
        senderSignature,
        hash,
        data.delegateKey
    )

    override fun getDataInstance(): DelegateTransactionData = DelegateTransactionData::class.java.newInstance()

}