package com.austin.kambapos.data.model

data class CartItemDTO(
    val id: Int,
    val name: String,
    val stockLabel: String = "Stock:",
    val stockQty: Int,
    val iva: Int?,
    val unitPrice: Int,
    val quantity: Int = 1,
    val imageUrl: String?
)