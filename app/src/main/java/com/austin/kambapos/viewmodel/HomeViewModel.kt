package com.austin.kambapos.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.austin.kambapos.data.model.UiStates.UserUiState
import com.austin.kambapos.data.repository.AuthRepository

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

import kotlinx.coroutines.launch


//
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: AuthRepository

) : ViewModel() {

    var uiState by mutableStateOf<UserUiState>(UserUiState.Loading)
        private set

    /// product
    init {
        observeUser()

    }

    private fun observeUser() {
        viewModelScope.launch {

            repository.getAuthenticatedUserLocal().collect { user ->
                uiState = if (user != null) {
                    UserUiState.Success(user)

                } else {
                    UserUiState.Error("Sessão expirada")
                }
            }
        }
    }

}
