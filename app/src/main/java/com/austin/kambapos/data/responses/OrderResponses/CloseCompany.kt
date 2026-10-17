package com.austin.kambapos.data.responses.OrderResponses

import com.google.gson.annotations.SerializedName

data class CloseCompany(
    val name: String? = null,
    val nif: String? = null,
    val address: String? = null,
    val phone: String? = null,
    @SerializedName("certificate_number")
    val certificateNumber: String? = null
)