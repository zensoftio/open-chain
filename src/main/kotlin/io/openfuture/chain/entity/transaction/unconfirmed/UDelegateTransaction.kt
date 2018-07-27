package io.openfuture.chain.entity.transaction.unconfirmed

import io.openfuture.chain.entity.transaction.DelegateTransaction
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "u_delegate_transactions")
class UDelegateTransaction(
    timestamp: Long,
    amount: Long,
    fee: Long,
    recipientAddress: String,
    senderAddress: String,
    senderPublicKey: String,
    senderSignature: String,
    hash: String,

    @Column(name = "delegate_key", nullable = false, unique = true)
    var delegateKey: String

) : UTransaction(timestamp, amount, fee, recipientAddress, senderPublicKey, senderAddress, senderSignature, hash) {

    override fun toConfirmed(): DelegateTransaction = DelegateTransaction(
        timestamp,
        amount,
        fee,
        recipientAddress,
        senderAddress,
        senderPublicKey,
        senderSignature,
        hash,
        delegateKey
    )

}