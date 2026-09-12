package com.austin.kambapos.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.austin.kambapos.data.local.entity.CartItemWithProduct
import com.austin.kambapos.data.repository.OrdersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val ordersRepository: OrdersRepository,
) : ViewModel() {

    private val _orderId = MutableStateFlow<Int?>(null)
    val orderId: StateFlow<Int?> = _orderId

    private val _cartItems = MutableStateFlow<List<CartItemWithProduct>>(emptyList())
    val cartItems = _cartItems.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _closeOrderState = MutableStateFlow<String?>(null)
    val closeOrderState = _closeOrderState.asStateFlow()

    private var openOrderJob: Job? = null
    private var observeOrderJob: Job? = null
    
    // Lista para gerenciar múltiplos pedidos de abertura simultâneos ou sequenciais rápidos
    private val pendingCallbacks = mutableListOf<(Int) -> Unit>()

    init {
        syncOrders()
    }

    fun syncOrders() {
        viewModelScope.launch {
            ordersRepository.syncOrders()
        }
    }

    /**
     * Abre uma venda e executa o callback [onComplete] com o ID da venda.
     * Se já houver uma venda aberta, executa o callback imediatamente.
     */
    fun openOrder(shiftId: Int, onComplete: ((Int) -> Unit)? = null) {
        val currentId = _orderId.value
        if (currentId != null) {
            onComplete?.invoke(currentId)
            return
        }

        // Adiciona o callback à lista de espera
        onComplete?.let { pendingCallbacks.add(it) }

        // Se já houver um job em andamento, não iniciamos outro
        if (openOrderJob?.isActive == true) return

        openOrderJob = viewModelScope.launch {
            try {
                val response = ordersRepository.getOrders(shiftId)
                val newId = response.id
                _orderId.value = newId
                Log.d("ORDER", "Venda iniciada com ID: $newId")
                
                // Executa e limpa os callbacks pendentes
                val callbacks = pendingCallbacks.toList()
                pendingCallbacks.clear()
                callbacks.forEach { it(newId) }
            } catch (e: Exception) {
                _errorMessage.value = "Erro ao iniciar venda"
                Log.e("ORDER_ERROR", e.message.toString())
                pendingCallbacks.clear()
            }
        }
    }

    private val _userId = MutableStateFlow<Int?>(null)

    fun setUserId(id: Int) {
        _userId.value = id
    }

    val orders = _userId
        .filterNotNull()
        .flatMapLatest { userId ->
            ordersRepository.getOrdersByUser(userId)
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val totalSales = orders
        .map { list -> list.sumOf { it.total } }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            0.0
        )

    val ordersCount = orders
        .map { it.size }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            0
        )

    fun closeOrder(
        orderId: Int?,
        paymentMethod: String,
        received: Double?,
        change: Double?
    ) {
        viewModelScope.launch {
            val result = ordersRepository.closeOrder(
                orderId,
                paymentMethod,
                received,
                change
            )
            result.onSuccess {
                _closeOrderState.value = it
                _orderId.value = null // Limpa para a próxima venda
            }.onFailure { error ->
                _closeOrderState.value = error.message ?: "Erro ao fechar venda"
                Log.e("OrderViewModel", "Erro ao fechar venda", error)
            }
        }
    }

    fun clearCloseOrderState() {
        _closeOrderState.value = null
    }

    // PDV: Busca a venda aberta vinculada ao turno
    fun observeCurrentOrder(shiftId: Int) {
        observeOrderJob?.cancel()
        observeOrderJob = viewModelScope.launch {
            orders.collect { allOrders ->
                val activeOrder = allOrders.find { it.shift_id == shiftId && it.status == "open" }
                if (activeOrder != null) {
                    _orderId.value = activeOrder.id
                } else if (_orderId.value != null && allOrders.none { it.id == _orderId.value && it.status == "open" }) {
                     _orderId.value = null
                }
            }
        }
    }

    fun observeOrders() {
        viewModelScope.launch {
            ordersRepository.observeOrders().collect { tableOrders ->
                val activeOrder = tableOrders.find { it.status == "open" }
                if (activeOrder != null) {
                    _orderId.value = activeOrder.id
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        observeOrderJob?.cancel()
        openOrderJob?.cancel()
    }
}
