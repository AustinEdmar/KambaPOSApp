package com.austin.kambapos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shifts")
data class ShiftEntity(

    @PrimaryKey
    val id: Int,

    val userId: Int,
    val userName: String?, // Alterado para opcional para suportar a rota 'open' que não envia o nome

    val initialAmount: Double?,

    val expectedCashAmount: Double?,
    val difference: Double?,
    val finalCashAmount: Double?,

    val grossSales: Double?,
    val refundTotal: Double?,
    val netSales: Double?,

    val terminalId: String?,

    val status: String?,

    val openedAt: String?,
    val closedAt: String?,

    val updatedAt: String?,
    val createdAt: String?
)