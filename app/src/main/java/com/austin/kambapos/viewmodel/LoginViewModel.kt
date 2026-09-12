package com.austin.kambapos.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.austin.kambapos.data.model.UiStates.LoginUiState
import com.austin.kambapos.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    var uiState by mutableStateOf<LoginUiState>(LoginUiState.Idle)
        private set

    fun login(email: String, password: String) {
        viewModelScope.launch {
            uiState = LoginUiState.Loading
            try {
                val response = repository.login(email, password)
                uiState = LoginUiState.Success(response)
                Log.d("LoginViewModel", "Login realizado com sucesso para: ${response.user.email}")
            } catch (e: retrofit2.HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorMessage = try {
                    val json = org.json.JSONObject(errorBody ?: "")
                    json.getString("message")
                } catch (ex: Exception) {
                    errorBody ?: "Erro no servidor"
                }
                uiState = LoginUiState.Error("Erro ${e.code()}: $errorMessage")
                Log.e("LoginViewModel", "Erro de API: $errorMessage")
            } catch (e: java.io.IOException) {
                uiState = LoginUiState.Error("Sem conexão. Verifique sua internet.")
                Log.e("LoginViewModel", "Erro de rede", e)
            } catch (e: Exception) {
                uiState = LoginUiState.Error("Erro inesperado: ${e.message}")
                Log.e("LoginViewModel", "Erro crítico no login", e)
            }
        }
    }
}
