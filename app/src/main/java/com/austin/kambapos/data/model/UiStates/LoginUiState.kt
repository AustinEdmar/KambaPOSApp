package com.austin.kambapos.data.model.UiStates

import com.austin.kambapos.data.responses.AuthResponses.AuthResponse

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val auth: AuthResponse) : LoginUiState()
    data class Error(val message: String) : LoginUiState()

}