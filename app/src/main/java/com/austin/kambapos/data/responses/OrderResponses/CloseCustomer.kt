package com.austin.kambapos.data.responses.OrderResponses

import com.google.gson.annotations.SerializedName

data class CloseCustomer(
    val name: String? = null,
    @SerializedName("tax_number")
    val taxNumber: String? = null,
    val address: String? = null
)
