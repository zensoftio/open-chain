package io.openfuture.chain.core.model.entity.state

import io.openfuture.chain.core.model.entity.base.BaseModel
import io.openfuture.chain.core.model.entity.block.Block
import io.openfuture.chain.network.message.base.Message
import javax.persistence.*

@Entity
@Table(name = "states")
@Inheritance(strategy = InheritanceType.JOINED)
abstract class State(

    @Column(name = "address", nullable = false)
    var address: String,

    @Column(name = "hash", nullable = false)
    var hash: String,

    @ManyToOne
    @JoinColumn(name = "block_id", nullable = false)
    var block: Block? = null

) : BaseModel() {

    abstract fun getBytes(): ByteArray

    abstract fun toMessage(): Message

}