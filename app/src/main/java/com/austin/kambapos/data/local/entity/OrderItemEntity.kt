package com.austin.kambapos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "order_items")
data class OrderItemEntity(
    @PrimaryKey val id: Int,
    val order_id: Int,
    val product_id: Int,
    val product_name: String?,
    val product_code: String?,
    val unit: String?,
    val quantity: Int,
    val unit_price: Double,
    val discount_percent: Double?,
    val discount_amount: Double?,
    val iva_rate: Double,
    val tax_code: String?,
    val iva_amount: Double,
    val subtotal: Double,
    val total_with_iva: Double,
    val status: String?,
    val product_name_local: String?,   // nome do produto (product.name)
    val product_image_path: String?    // product.image_path
)