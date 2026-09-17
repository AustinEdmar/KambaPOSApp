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
        Log.d("REPO", "Adicionando ao carrinho: $product")
        cartDao.addToCartTransaction(orderId, product)
        scheduler.schedule()
    }

    suspend fun increaseQuantity(item: CartItemEntity) {
        cartDao.increaseQuantityTransaction(item)
        scheduler.schedule()
    }

    suspend fun decreaseQuantity(item: CartItemEntity) {
        cartDao.decreaseQuantityTransaction(item)
        scheduler.schedule()
    }

    suspend fun syncCart(): Boolean = syncMutex.withLock {
        Log.d("REPO", "Iniciando syncCart")
        var hasError = false

        while (true) {
            val items = cartDao.getPendingItems()
            if (items.isEmpty()) break
            Log.d("REPO", "Items pendentes: ${items.size}")

            for (item in items) {
                val deltaToSync = item.delta
                val snapshotVersion = item.syncVersion

                if (deltaToSync == 0) {
                    // Delta zerado mas ainda marcado como pendente — limpar
                    cartDao.update(item.copy(pendingSync = false))
                    continue
                }

                try {
                    // ✅ PESSIMISTA: Chama a API ANTES de modificar o Room
                    val response = if (deltaToSync > 0) {
                        api.addItem(
                            item.orderId,
                            AddItemRequest(item.productId, deltaToSync)
                        )
                    } else {
                        api.decrementItem(
                            item.orderId,
                            AddItemRequest(item.productId, kotlin.math.abs(deltaToSync))
                        )
                    }

                    Log.d("REPO", "Resposta da API: ${response.code()}")

                    if (!response.isSuccessful) {
                        val raw = response.errorBody()?.string()
                        Log.e("SYNC", "Error body raw: $raw")
                        // API retornou erro — mantém o delta para retry
                        hasError = true
                        continue
                    }

                    // ✅ Só zera o delta APÓS confirmação de sucesso
                    // Verifica se o syncVersion mudou (usuário modificou durante sync)
                    withContext(NonCancellable) {
                        val current = cartDao.getItem(item.orderId, item.productId)
                            ?: return@withContext

                        if (current.syncVersion == snapshotVersion) {
                            // Ninguém tocou — zera delta e marca como sincronizado
                            cartDao.update(current.copy(delta = 0, pendingSync = false))
                        } else {
                            // Usuário modificou durante o sync
                            // Subtrai apenas o delta que foi sincronizado, mantém o restante
                            val remainingDelta = current.delta - deltaToSync
                            cartDao.update(current.copy(
                                delta = remainingDelta,
                                pendingSync = remainingDelta != 0
                            ))
                        }

                        // Limpeza segura — só deleta se quantidade chegou a 0 e não há delta pendente
                        val afterUpdate = cartDao.getItem(item.orderId, item.productId)
                            ?: return@withContext
                        if (afterUpdate.quantity <= 0 && afterUpdate.delta == 0) {
                            cartDao.deleteItem(afterUpdate.id, afterUpdate.orderId)
                            cartDao.deleteProductIfNotInCart(afterUpdate.productId)
                            cartDao.deleteOrderIfNotInCart(afterUpdate.orderId)
                        }
                    }

                } catch (e: Exception) {
                    // Erro de rede/reboot — delta permanece intacto no Room, retry pelo WorkManager
                    Log.e("REPO", "Erro na chamada de API — delta preservado para retry", e)
                    hasError = true
                }
            }

            if (hasError) break
        }

        return !hasError
    }



    fun observeCart(orderId: Int?) =
        cartDao.getCart(orderId)
}
