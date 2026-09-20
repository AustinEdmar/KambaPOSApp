package com.austin.kambapos.data.repository

import android.util.Log
import com.austin.kambapos.data.api.OrdersApi
import com.austin.kambapos.data.local.dao.CartDao
import com.austin.kambapos.data.local.dao.OrderDao
import com.austin.kambapos.data.local.entity.OrderEntity
import com.austin.kambapos.data.local.mapper.toEntity
import com.austin.kambapos.data.model.OrderResquest.CloseOrderRequest
import com.austin.kambapos.data.model.OrderResquest.OpenOrderRequest
import com.austin.kambapos.data.model.OrdersDTO
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OrdersRepository @Inject constructor (
    private val api: OrdersApi,
    private val orderDao: OrderDao,
    private val cartDao: CartDao
){

    suspend fun getOrders(shiftId: Int): OrdersDTO {
        val response = api.getOrder(OpenOrderRequest(0))
        Log.d("ORDER_DEBUG", "openOrder retornou id=${response.id} status=${response.status} total=${response.total}")

        if (response.status == "open") {
            orderDao.insert(response.toEntity())
        }
        return response
    }

    fun getOrdersByUser(
        userId: Int,
        shiftId: Int
    ): Flow<List<OrderEntity>> {
        return orderDao.getOrdersByUser(userId, shiftId)
    }

    suspend fun closeOrder(
        orderId: Int?,
        paymentMethod: String,
        received: Double?,
        change: Double?
    ): Result<String> {
        if (orderId == null) {
            return Result.failure(Exception("ID da venda não encontrado"))
        }

        return try {
            val response = api.closeOrder(
                orderId,
                CloseOrderRequest(
                    payment_method = paymentMethod,
                    table_id = 0, // Sem mesa
                    received = received,
                    change = change
                )
            )

            if (response.isSuccessful) {
                val message = response.body()?.message ?: "Pedido fechado"
                cartDao.clearCart(orderId)
                orderDao.deleteOrderById(orderId)
                Result.success(message)
            } else {
                val errorMsg = response.errorBody()?.string() ?: "Erro ao fechar pedido"
                Log.e("OrdersRepository", "Erro API closeOrder: $errorMsg")
                Result.failure(Exception(errorMsg))
            }
        } catch (e: Exception) {
            Log.e("OrdersRepository", "Exceção em closeOrder", e)
            Result.failure(e)
        }
    }

    fun observeOrders() = orderDao.observeOrders()

    suspend fun syncOrders() {
        try {
            val remoteOrders = api.getOrders()
            Log.d("ORDER_DEBUG", "syncOrders trouxe ${remoteOrders} ordens")

            orderDao.insertOrders(remoteOrders.map { it.toEntity() })

            remoteOrders.forEach { dto ->
                orderDao.clearItemsForOrder(dto.id)
                orderDao.insertItems(dto.items.filterNotNull().map { it.toEntity(dto.id) })
            }
        } catch (e: Exception) {
            Log.e("ORDER_DEBUG", "syncOrders falhou", e)
        }
    }

    fun observeOrdersWithItems(userId: Int) = orderDao.observeOrdersWithItems(userId)
}
