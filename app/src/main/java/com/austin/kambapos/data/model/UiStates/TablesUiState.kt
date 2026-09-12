package com.austin.kambapos.data.model.UiStates

import com.austin.kambapos.data.local.entity.TableEntity

sealed class TablesUiState {
    object Loading : TablesUiState()
    data class Success(val tables: List<TableEntity>) : TablesUiState()
    data class Error(val message: String) : TablesUiState()
}
