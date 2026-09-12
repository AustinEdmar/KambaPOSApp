package com.austin.kambapos.data.responses.ShiftsResponses

import com.google.gson.annotations.SerializedName

data class CloseShiftResponse (
    val message: String?,
    @SerializedName("expected_cash")
    val expectedCash: Int?,
    @SerializedName("final_cash")
    val finalCash: Int?

)



//"message": "Turno fechado com sucesso.",
//	"expected_cash": 11539,
//	"final_cash": 1539,
//	"difference": -10000