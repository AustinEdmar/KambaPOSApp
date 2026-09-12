package com.austin.kambapos.data.model

import com.google.gson.annotations.SerializedName

data class TaxRateDTO(
    val id: Int,

    @SerializedName("tax_code")
    val taxCode: String,

    @SerializedName("tax_percentage")
    val taxPercentage: String
)