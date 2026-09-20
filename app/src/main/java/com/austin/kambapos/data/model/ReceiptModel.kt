package com.austin.kambapos.data.model

data class ReceiptModel(
    val items: List<CartItemDTO>,
    val subtotal: Double,
    val tax: Double,
    val total: Double,
    val tableId: Int,
    val orderId: Int?,
    val paymentMethod: String,
    val paidAmount: Double? = null,  // ✅ Double? (nullable)
    val change: Double? = null,    // ✅ Double? (nullable)
    val copyType: String = "ORIGINAL" // 👈 AQUI
)