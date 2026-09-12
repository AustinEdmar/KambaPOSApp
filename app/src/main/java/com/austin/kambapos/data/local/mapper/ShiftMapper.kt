package com.austin.kambapos.data.local.mapper

import com.austin.kambapos.data.local.entity.ShiftEntity
import com.austin.kambapos.data.responses.ShiftsResponses.ShiftResponse

fun ShiftResponse.toEntity(): ShiftEntity {
    return ShiftEntity(
        id = id,

        userId = user?.id ?: userId, // Usa o ID do objeto user ou o campo userId direto
        userName = user?.name ?: "", // O nome pode vir vazio se o objeto user não existir

        initialAmount = initialAmount?.toDoubleOrNull() ?: 0.0,

        expectedCashAmount = expectedCashAmount?.toDoubleOrNull(),
        difference = difference?.toDoubleOrNull(),
        finalCashAmount = finalCashAmount?.toDoubleOrNull(),

        grossSales = grossSales?.toDoubleOrNull(),
        refundTotal = refundTotal?.toDoubleOrNull(),
        netSales = netSales?.toDoubleOrNull(),

        terminalId = terminalId,

        status = status,

        openedAt = openedAt,
        closedAt = closedAt,

        updatedAt = updatedAt,
        createdAt = createdAt
    )
}