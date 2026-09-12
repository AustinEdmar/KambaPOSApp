package com.austin.kambapos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: Int,
    val user_id: Int,
    val shift_id: Int,
    val status: String,
    val subtotal: Double,
    val total: Double,
    val opened_at: String?,
    val updated_at: String?,
    val created_at: String?
)
