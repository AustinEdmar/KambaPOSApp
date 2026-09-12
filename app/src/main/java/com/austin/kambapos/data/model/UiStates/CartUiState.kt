package com.austin.kambapos.data.model.UiStates

sealed class CartUiState {
    object Idle    : CartUiState()
    object Loading : CartUiState()
    data class NetworkError(val message: String)  : CartUiState()
    data class ServerError(val message: String)   : CartUiState()
    data class UnknownError(val message: String)  : CartUiState()
}