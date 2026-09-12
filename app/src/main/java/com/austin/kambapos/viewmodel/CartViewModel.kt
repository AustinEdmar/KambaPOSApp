package com.austin.kambapos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.austin.kambapos.data.local.entity.CartItemWithProduct
import com.austin.kambapos.data.local.entity.ProductEntity
import com.austin.kambapos.data.repository.CartRepository

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn


import javax.inject.Inject

import kotlinx.coroutines.launch

//
@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {

    private val orderIdFlow = MutableStateFlow<Int?>(null)

    // 👇 CART 100% REATIVO (SEM JOBS, SEM OBSERVE MANUAL)
    val cartItems = orderIdFlow
        .filterNotNull()
        .distinctUntilChanged()
        .flatMapLatest { orderId ->
            cartRepository.observeCart(orderId)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    // contador automático
    val cartCount = cartItems
        .map { list -> list.sumOf { it.cartItem.quantity } }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            0
        )

    fun setOrderId(orderId: Int?) {
        orderIdFlow.value = orderId
    }

    fun addProduct(product: ProductEntity, orderId: Int?) {
        viewModelScope.launch {
            cartRepository.addToCart(orderId, product)
        }
    }

    fun increaseQuantity(item: CartItemWithProduct) {
        viewModelScope.launch {
            cartRepository.increaseQuantity(item.cartItem)
        }
    }

    fun decreaseQuantity(item: CartItemWithProduct) {
        viewModelScope.launch {
            cartRepository.decreaseQuantity(item.cartItem)
        }
    }
}