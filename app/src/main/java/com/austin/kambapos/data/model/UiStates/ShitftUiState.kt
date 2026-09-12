package com.austin.kambapos.data.model.UiStates

import com.austin.kambapos.data.responses.ShiftsResponses.ShiftResponse

sealed class ShitftUiState {
    object Idle : ShitftUiState()
    object Loading : ShitftUiState()
    data class Success(val shift: ShiftResponse) : ShitftUiState()
    data class Error(val message: String) : ShitftUiState()
}