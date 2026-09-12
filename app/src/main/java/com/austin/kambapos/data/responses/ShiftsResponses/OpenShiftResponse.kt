package com.austin.kambapos.data.responses.ShiftsResponses

import com.google.gson.annotations.SerializedName

data class OpenShiftResponse(
    val message: String?,
    @SerializedName("expected_cash")
    val expectedCash: Int?,
    @SerializedName("final_cash")
    val finalCash: Int?,
    @SerializedName("data")
    val shift: ShiftResponse?
)
