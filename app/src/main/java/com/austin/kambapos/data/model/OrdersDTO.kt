package com.austin.kambapos.data.model

data class OrdersDTO(
    val id: Int,
    val user_id: Int,
    val shift_id: Int,
    val customer_id: Int?,
    val status: String,

    val subtotal: Double,
    val iva: Double?,
    val discount: Double?,
    val total: Double,

    val invoice_generated: Boolean?,
    val notes: String?,

    val opened_at: String?,
    val closed_at: String?,
    val created_at: String?,
    val updated_at: String?,

    val items: List<OrderItemDTO?>,
    val user: OrderUserDTO?
)

data class OrderItemDTO(
    val id: Int,
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
    val tax_exemption_reason: String?,
    val iva_amount: Double,

    val subtotal: Double,
    val total_with_iva: Double,
    val status: String?,

    val created_at: String?,
    val updated_at: String?,

    val product: OrderItemProductDTO?
)

// Versão simplificada — é o que a API realmente devolve dentro de "items"
data class OrderItemProductDTO(
    val id: Int,
    val name: String,
    val image_path: String?
)

data class OrderUserDTO(
    val id: Int,
    val name: String
)