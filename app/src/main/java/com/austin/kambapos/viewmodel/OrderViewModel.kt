package com.austin.kambapos.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.austin.kambapos.data.local.entity.CartItemWithProduct
import com.austin.kambapos.data.local.entity.OrderWithItems
import com.austin.kambapos.data.repository.OrdersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlinx.coroutines.launch
import kotlin.collections.emptyList

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

    private val pendingCallbacks = mutableListOf<(Int) -> Unit>()

    private val _userId = MutableStateFlow<Int?>(null)
    private val _shiftId = MutableStateFlow<Int?>(null)

    init {
        syncOrders()
    }

    fun syncOrders() {
        viewModelScope.launch {
            ordersRepository.syncOrders()
        }
    }

    fun setUserId(id: Int) {
        _userId.value = id
    }



    fun openOrder(shiftId: Int, onComplete: ((Int) -> Unit)? = null) {
        val currentId = _orderId.value
        _shiftId.value = shiftId   // adicionar
        if (currentId != null) {
            onComplete?.invoke(currentId)
            return
        }

        onComplete?.let { pendingCallbacks.add(it) }

        if (openOrderJob?.isActive == true) return

        openOrderJob = viewModelScope.launch {
            try {
                val response = ordersRepository.getOrders(shiftId)
                val newId = response.id
                _orderId.value = newId
                Log.d("ORDER", "Venda iniciada com ID: $newId")

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

    fun setShiftId(id: Int) {
        _shiftId.value = id
    }

    // Lista simples (para a LazyColumn de cards)
    val orders = combine(
        _userId.filterNotNull(),
        _shiftId.filterNotNull()
    ) { userId, shiftId -> userId to shiftId }
        .flatMapLatest { (userId, shiftId) ->
            ordersRepository.getOrdersByUser(userId, shiftId)
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    // Lista com itens (para o modal de detalhes) — lida direto do Room, funciona offline
    val ordersWithItems: StateFlow<List<OrderWithItems>> = _userId
        .filterNotNull()
        .flatMapLatest { userId ->
            ordersRepository.observeOrdersWithItems(userId)
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    // Só vendas fechadas contam como "venda" nas stats
    private val closedOrders = orders
        .map { list -> list.filter { it.status == "closed" } }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

    val totalSales = closedOrders
        .map { list -> list.sumOf { it.total } }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            0.0
        )

    val ordersCount = closedOrders
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
                _orderId.value = null
            }.onFailure { error ->
                _closeOrderState.value = error.message ?: "Erro ao fechar venda"
                Log.e("OrderViewModel", "Erro ao fechar venda", error)
            }
        }
    }

    fun clearCloseOrderState() {
        _closeOrderState.value = null
    }

    fun observeCurrentOrder(shiftId: Int) {
        _shiftId.value = shiftId   // adicionar
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