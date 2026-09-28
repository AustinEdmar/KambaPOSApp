package com.austin.kambapos.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.austin.kambapos.data.local.entity.CartItemWithProduct
import com.austin.kambapos.data.local.entity.ProductEntity
import com.austin.kambapos.data.repository.CartRepository
import com.austin.kambapos.data.repository.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val usersRepository: UsersRepository
) : ViewModel() {

    private val orderIdFlow = MutableStateFlow<Int?>(null)
    private val _authFeedback = MutableStateFlow<String?>(null)
    val authFeedback: StateFlow<String?> = _authFeedback

    private val _authSuccess = MutableStateFlow(false)
    val authSuccess: StateFlow<Boolean> = _authSuccess

    val cartItems = orderIdFlow
        .filterNotNull()
        .distinctUntilChanged()
        .flatMapLatest { orderId -> cartRepository.observeCart(orderId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val cartCount = cartItems
        .map { list -> list.sumOf { it.cartItem.quantity } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    fun setOrderId(orderId: Int?) {
        orderIdFlow.value = orderId
    }

    fun addProduct(product: ProductEntity, orderId: Int?) {
        viewModelScope.launch { cartRepository.addToCart(orderId, product) }
    }

    fun increaseQuantity(item: CartItemWithProduct) {
        viewModelScope.launch { cartRepository.increaseQuantity(item.cartItem) }
    }

    fun onPinSubmitted(pin: String, item: CartItemWithProduct?) {
        viewModelScope.launch {
            try {
                val user = usersRepository.findByPin(pin)

                if (user == null) {
                    _authFeedback.value = "PIN inválido"
                    return@launch
                }

                _authFeedback.value = "Autorizado por: ${user.name}"

                item?.let {
                    Log.d("CartViewModel", "Chamando decreaseQuantity para item ${it.cartItem.id}")
                    cartRepository.decreaseQuantity(it.cartItem, pin)
                    Log.d("CartViewModel", "decreaseQuantity concluído com sucesso")
                    cartRepository.syncCart()
                    Log.d("CartViewModel", "syncCart concluído")
                }
            } catch (e: Exception) {
                Log.e("CartViewModel", "ERRO no fluxo de autorização", e)
            }
        }
    }

    fun clearAuthFeedback() {
        _authFeedback.value = null
    }
}