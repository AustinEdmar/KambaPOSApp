package com.austin.kambapos.data.repository

import com.austin.kambapos.data.api.ShiftApi
import com.austin.kambapos.data.datastore.TokenManager
import com.austin.kambapos.data.local.dao.OrderDao
import com.austin.kambapos.data.local.dao.ShiftDao
import com.austin.kambapos.data.local.entity.ShiftEntity
import com.austin.kambapos.data.local.mapper.toEntity
import com.austin.kambapos.data.model.ShiftRequest.ShiftRequest
import com.austin.kambapos.data.responses.ShiftsResponses.ShiftResponse
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow

class ShiftRepository @Inject constructor(
    private val api: ShiftApi,
    private val shiftDao: ShiftDao,
    private val orderDao: OrderDao,
    private val tokenManager: TokenManager
) {

    suspend fun shift(initial_amount: Double?, final_cash_amount: Double?): ShiftResponse {
        val response = api.open(ShiftRequest(initial_amount, final_cash_amount))
        // API retorna o objeto Shift diretamente, então inserimos o response.
        shiftDao.insert(response.toEntity())
        return response
    }


    suspend fun CloseShift(initial_amount: Double?, final_cash_amount: Double?, userId: Int): ShiftResponse {
        val response = api.close(ShiftRequest(initial_amount, final_cash_amount))
        orderDao.clearOrders()
        shiftDao.clearAll()
        return response
    }




    // 👇 AQUI está o shiftFlow
    val shiftFlow: Flow<ShiftEntity?> =
        shiftDao.observeShift()

    suspend fun syncShift() {
        try {
            val response = api.shiftCurrent()
            // Como a API retorna o objeto Shift diretamente na raiz, inserimos diretamente
            shiftDao.insert(response.toEntity())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    suspend fun clearShift() {
        shiftDao.clearAll()
    }

}
