package com.austin.kambapos.data.responses.OrderResponses

data class OrderActionResponse(
    val message: String,
    val error: String? = null  // para capturar erros do servidor também
)
