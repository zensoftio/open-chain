package io.openfuture.chain.domain.transaction.data

class TransferTransactionData(
    amount: Double,
    fee: Double,
    recipientAddress: String,
    senderAddress: String
) : BaseTransactionData(amount, fee, recipientAddress, senderAddress) {

    override fun getBytes(): ByteArray {
        val builder = StringBuilder()
        builder.append(amount)
        builder.append(fee)
        builder.append(recipientAddress)
        builder.append(senderAddress)
        return builder.toString().toByteArray()
    }

}