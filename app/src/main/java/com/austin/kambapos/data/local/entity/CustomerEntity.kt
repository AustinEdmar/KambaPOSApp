package com.austin.kambapos.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "customers", indices = [Index("taxNumber", unique = true)])
data class CustomerEntity(
    @PrimaryKey val id: Long,          // id do servidor, não autogenerate
    val name: String,
    val taxNumber: String?,
    val phone: String?,
    val address: String?,
    val isFinalConsumer: Boolean
)
