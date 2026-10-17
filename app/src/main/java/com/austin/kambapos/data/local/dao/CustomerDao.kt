package com.austin.kambapos.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.austin.kambapos.data.local.entity.CustomerEntity

@Dao
interface CustomerDao {
    @Query("SELECT * FROM customers WHERE taxNumber = :nif LIMIT 1")
    suspend fun findByNif(nif: String): CustomerEntity?

    @Query("SELECT * FROM customers WHERE name LIKE '%' || :q || '%' OR taxNumber LIKE '%' || :q || '%' LIMIT 20")
    suspend fun search(q: String): List<CustomerEntity>

    @Upsert
    suspend fun upsert(customer: CustomerEntity)
}