package com.austin.kambapos.data.responses.ShiftsResponses

import com.austin.kambapos.data.responses.AuthResponses.UserDto
import com.google.gson.annotations.SerializedName

data class ShiftResponse(
    @SerializedName("id")
    val id: Int,

    @SerializedName("user_id")
    val userId: Int,

    @SerializedName("initial_amount")
    val initialAmount: String?,

    @SerializedName("expected_cash_amount")
    val expectedCashAmount: String?,

    @SerializedName("difference")
    val difference: String?,

    @SerializedName("gross_sales")
    val grossSales: String?,

    @SerializedName("refund_total")
    val refundTotal: String?,

    @SerializedName("net_sales")
    val netSales: String?,

    @SerializedName("final_cash_amount")
    val finalCashAmount: String?,

    @SerializedName("status")
    val status: String?,

    @SerializedName("terminal_id")
    val terminalId: String?,

    @SerializedName("opened_at")
    val openedAt: String?,

    @SerializedName("closed_at")
    val closedAt: String?,

    @SerializedName("created_at")
    val createdAt: String?,

    @SerializedName("updated_at")
    val updatedAt: String?,

    @SerializedName("user")
    val user: UserDto? // Agora opcional para evitar erro se a API não enviar no open
)