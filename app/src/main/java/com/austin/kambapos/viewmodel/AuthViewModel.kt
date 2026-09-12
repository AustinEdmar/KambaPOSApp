package com.austin.kambapos.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.austin.kambapos.data.model.UiStates.AuthUiState
import com.austin.kambapos.data.repository.AuthRepository
import com.austin.kambapos.data.repository.ShiftRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch


@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val shiftRepository: ShiftRepository
) : ViewModel() {

    var uiState by mutableStateOf<AuthUiState>(AuthUiState.Loading)
        private set

    init {
        checkAuth()
    }

    fun checkAuth() {
        viewModelScope.launch {
            uiState = AuthUiState.Loading

            combine(
                repository.hasToken(),
                repository.shouldLogout()
            ) { hasToken, expired ->
                when {
                    !hasToken -> AuthUiState.Unauthenticated
                    expired -> {
                        repository.logout()
                        AuthUiState.Unauthenticated
                    }
                    else -> {
                        AuthUiState.Authenticated(
                            repository.getAuthenticatedUserLocal()
                        )
                    }
                }
            }.collect { state ->
                uiState = state
            }



        }
    }

    //clearShift
    fun logout() {
        viewModelScope.launch {
            repository.logout()
            shiftRepository.clearShift()
            uiState = AuthUiState.Unauthenticated
        }
    }
}
