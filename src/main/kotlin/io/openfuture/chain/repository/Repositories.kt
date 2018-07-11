package io.openfuture.chain.repository

import io.openfuture.chain.entity.*
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.stereotype.Repository

@NoRepositoryBean
interface BaseRepository<T> : JpaRepository<T, Int>

@Repository
interface BlockRepository : BaseRepository<Block> {

    fun findByHash(hash: String): Block?

    fun findFirstByOrderByHeightDesc(): Block?

}

@Repository
interface TransactionRepository : BaseRepository<Transaction>

@Repository
interface SeedWordRepository : BaseRepository<SeedWord> {

    fun findOneByIndex(index: Int): SeedWord

    fun findOneByValue(value: String): SeedWord?

}
