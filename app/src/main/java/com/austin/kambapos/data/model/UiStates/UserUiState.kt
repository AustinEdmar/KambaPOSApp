package com.austin.kambapos.data.model.UiStates

import com.austin.kambapos.data.responses.AuthResponses.UserDto

sealed class UserUiState {
    object Loading : UserUiState()
    data class Success(val user: UserDto) : UserUiState()
    data class Error(val message: String) : UserUiState()
}