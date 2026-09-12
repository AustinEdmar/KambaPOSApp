package com.austin.kambapos.data.model.OrderResquest

data class CloseOrderRequest(
    val payment_method: String,
    val table_id: Int,
    val received: Double?,
    val change: Double?
)