package com.austin.kambapos.data.model.OrderResquest

data class CloseOrderRequest(
    val payment_method: String,

    val received: Double?,
    val change: Double?,
    val customer_id: Long? = null,
    val document_type: String? = null
)