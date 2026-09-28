package com.austin.kambapos.data.model.OrderResquest

data class AddItemRequest(
    val product_id: Int,
    val quantity: Int,
    val authorization_pin: String? = null
)