package com.austin.kambapos.data.repository

import android.util.Log
import com.austin.kambapos.data.api.OrdersApi
import com.austin.kambapos.data.local.dao.CartDao
import com.austin.kambapos.data.local.entity.CartItemEntity
import com.austin.kambapos.data.local.entity.ProductEntity
import com.austin.kambapos.data.model.OrderResquest.AddItemRequest
import com.austin.kambapos.worker.CartSyncScheduler
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CartRepository @Inject constructor (
    private val api: OrdersApi,
    private val cartDao: CartDao,
    private val scheduler: CartSyncScheduler
){
    private val syncMutex = Mutex()

    private val _navigationEvent = MutableSharedFlow<Unit>(
        replay = 1,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val navigationEvent = _navigationEvent.asSharedFlow()

    suspend fun addToCart(orderId: Int?, product: ProductEntity) {

        cartDao.addToCartTransaction(orderId, product)
        scheduler.schedule()
    }

    suspend fun increaseQuantity(item: CartItemEntity) {
        cartDao.increaseQuantityTransaction(item)
        scheduler.schedule()
    }

    suspend fun decreaseQuantity(item: CartItemEntity, pin: String?) {
        cartDao.decreaseQuantityTransaction(item, pin)
        scheduler.schedule()
    }

    suspend fun syncCart(): Boolean = syncMutex.withLock {
        var hasError = false

        while (true) {
            val items = cartDao.getPendingItems()
            Log.d("REPOSYNC", "tem sync: $items")
            if (items.isEmpty()) break

            for (item in items) {
                val deltaToSync = item.delta
                val snapshotVersion = item.syncVersion

                if (deltaToSync == 0) {
                    cartDao.update(item.copy(pendingSync = false))
                    continue
                }

                try {
                    Log.d("REPOSYNC", "Enviando para API: productId=${item.productId}, delta=$deltaToSync, pin=${item.pendingAuthorizationPin}")

                    val response = if (deltaToSync > 0) {
                        api.addItem(item.orderId, AddItemRequest(item.productId, deltaToSync))
                    } else {
                        api.decrementItem(
                            item.orderId,
                            AddItemRequest(
                                product_id = item.productId,
                                quantity = kotlin.math.abs(deltaToSync),
                                authorization_pin = item.pendingAuthorizationPin
                            )
                        )
                    }

                    Log.d("REPOSYNC", "Resposta da API: code=${response.code()}, success=${response.isSuccessful}")

                    if (!response.isSuccessful) {
                        val raw = response.errorBody()?.string()
                        Log.e("REPOSYNC", "Erro da API: $raw")
                        hasError = true
                        continue
                    }

                    withContext(NonCancellable) {
                        val current = cartDao.getItem(item.orderId, item.productId) ?: return@withContext
                        if (current.syncVersion == snapshotVersion) {
                            cartDao.update(
                                current.copy(delta = 0, pendingSync = false, pendingAuthorizationPin = null)
                            )
                        } else {
                            val remainingDelta = current.delta - deltaToSync
                            cartDao.update(
                                current.copy(
                                    delta = remainingDelta,
                                    pendingSync = remainingDelta != 0,
                                    pendingAuthorizationPin = if (remainingDelta != 0) current.pendingAuthorizationPin else null
                                )
                            )
                        }

                        val afterUpdate = cartDao.getItem(item.orderId, item.productId) ?: return@withContext
                        if (afterUpdate.quantity <= 0 && afterUpdate.delta == 0) {
                            cartDao.deleteItem(afterUpdate.id, afterUpdate.orderId)
                            cartDao.deleteProductIfNotInCart(afterUpdate.productId)
                            cartDao.deleteOrderIfNotInCart(afterUpdate.orderId)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("REPOSYNC", "Exceção ao chamar API", e)
                    hasError = true
                }
            }
            if (hasError) break
        }
        return !hasError
    }

    fun observeCart(orderId: Int?) = cartDao.getCart(orderId)
}
