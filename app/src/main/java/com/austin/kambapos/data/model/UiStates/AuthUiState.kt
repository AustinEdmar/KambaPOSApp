package com.austin.kambapos.data.model.UiStates

import com.austin.kambapos.data.responses.AuthResponses.UserDto
import kotlinx.coroutines.flow.Flow

sealed class AuthUiState {
    object Loading : AuthUiState()

    object Unauthenticated : AuthUiState()
    data class Authenticated(val user: Flow<UserDto?>) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}