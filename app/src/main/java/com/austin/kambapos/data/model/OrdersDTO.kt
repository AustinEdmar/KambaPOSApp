package com.austin.kambapos.data.model

data class OrdersDTO(
    val id: Int,

    val user_id: Int,
    val shift_id: Int,
    val status: String,
    val kitchen_status: String?,

    val subtotal: Double,
    val iva: Double,
    val discount: Double,
    val total: Double,

    val opened_at: String?,
    val closed_at: String?,
    val created_at: String?,
    val updated_at: String?,

    val tables: TableDTO?,
    val items: List<OrderItemDTO?>
)


data class TableDTO(
    val id: Int,
    val number: Int,
    val status: String
)

data class OrderItemDTO(
    val id: Int,
    val order_id: Int,
    val product_id: Int,
    val quantity: Int,
    val unit_price: Double,
    val iva_rate: Double,
    val iva_amount: Double,
    val subtotal: Double,
    val total_with_iva: Double,

    val product: ProductDTO
)
