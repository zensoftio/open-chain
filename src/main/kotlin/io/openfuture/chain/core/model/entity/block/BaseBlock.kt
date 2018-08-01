package io.openfuture.chain.core.model.entity.block

import io.openfuture.chain.core.model.entity.base.BaseModel
import javax.persistence.*

@Entity
@Table(name = "blocks")
@Inheritance(strategy = InheritanceType.JOINED)
abstract class BaseBlock(

    @Column(name = "height", nullable = false)
    var height: Long,

    @Column(name = "previous_hash", nullable = false)
    var previousHash: String,

    @Column(name = "timestamp", nullable = false)
    var timestamp: Long,

    @Column(name = "reward", nullable = false)
    var reward: Long,

    @Column(name = "public_key", nullable = false)
    val publicKey: String,

    @Column(name = "hash", nullable = false, unique = true)
    var hash: String,

    @Column(name = "signature", nullable = false)
    var signature: String? = null

) : BaseModel() {

    abstract fun sign(privateKey: ByteArray): BaseBlock

}