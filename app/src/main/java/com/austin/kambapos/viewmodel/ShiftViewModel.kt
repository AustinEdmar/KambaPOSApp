package com.austin.kambapos.viewmodel


import retrofit2.HttpException
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.austin.kambapos.data.model.UiStates.AuthUiState
import com.austin.kambapos.data.model.UiStates.ShitftUiState
import com.austin.kambapos.data.repository.AuthRepository
import com.austin.kambapos.data.repository.OrdersRepository
import com.austin.kambapos.data.repository.ShiftRepository
import com.austin.kambapos.utils.parseErrorMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import javax.inject.Inject
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


//
@HiltViewModel
class ShiftViewModel @Inject constructor(
    private val shiftRepository: ShiftRepository,
    private val ordersRepository: OrdersRepository,
    private val repository: AuthRepository,

    ) : ViewModel() {


    var uiState by mutableStateOf<AuthUiState>(AuthUiState.Loading)
        private set
    var shitftUiState by mutableStateOf<ShitftUiState>(ShitftUiState.Idle)
        private set

    init {

        syncShiftOnStart()

    }



    fun syncOrders() {
        viewModelScope.launch {
            ordersRepository.syncOrders()
        }
    }

    val shift = shiftRepository.shiftFlow
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

    private fun syncShiftOnStart() {
        viewModelScope.launch {
            shiftRepository.syncShift()
        }
    }



    fun shift(initial_amount: Double?, final_cash_amount: Double?){

           viewModelScope.launch {
               shitftUiState = ShitftUiState.Loading
               try {
                    val response = shiftRepository.shift(initial_amount, final_cash_amount)

                   shitftUiState = ShitftUiState.Success(response)

                    Log.d("HomeViewModel", "Shift OK: $response")
                   Log.d("HomeViewModel", "Shift OK: $shitftUiState")
                } catch (e: Exception) {
                    shitftUiState = ShitftUiState.Error("Erro ao abrir turno: ${e.message}")
                    Log.d("HomeViewModel", "Shift Error: ${e.message}")
                }

            }
       }


    fun logout() {
        viewModelScope.launch {
            repository.logout()
            shiftRepository.clearShift()
            uiState = AuthUiState.Unauthenticated
        }
    }

    fun CloseShift(initial_amount: Double?, final_cash_amount: Double?, userId: Int) {
        viewModelScope.launch {
            shitftUiState = ShitftUiState.Loading
            try {
                val response = shiftRepository.CloseShift(initial_amount, final_cash_amount, userId)
                shitftUiState = ShitftUiState.Success(response)
                Log.d("shiftViewModel", "Shift OK: $response")
                syncOrders()
                logout()
            } catch (e: HttpException) {
                // ✅ Captura erros HTTP (400, 404, 500...) e lê a mensagem real
                val errorMessage = e.parseErrorMessage()
                shitftUiState = ShitftUiState.Error(errorMessage)
                Log.d("shiftViewModel", "HTTP Error: $errorMessage")
            } catch (e: Exception) {
                shitftUiState = ShitftUiState.Error("Erro ao fechar turno: ${e.message}")
                Log.d("shiftViewModel", "Shift Error: ${e.message}")
            }
        }
    }









}
