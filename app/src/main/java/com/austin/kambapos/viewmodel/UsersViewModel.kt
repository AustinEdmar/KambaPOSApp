package com.austin.kambapos.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.austin.kambapos.data.local.entity.UsersEntity
import com.austin.kambapos.data.repository.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


@HiltViewModel
class UsersViewModel @Inject constructor(
    private val repository: UsersRepository
) : ViewModel() {

    val users: StateFlow<List<UsersEntity>> = repository.getLocalUsers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        Log.d("UsersViewModel", ">>> ViewModel Criado - Iniciando syncUsers")
        syncUsers()
    }

    fun syncUsers() {
        viewModelScope.launch {
            try {
                Log.d("UsersViewModel", "Solicitando sincronização ao repositório...")
                repository.syncUsers()
                Log.d("UsersViewModel", "Sincronização finalizada no ViewModel")
            } catch (e: Exception) {
                Log.e("UsersViewModel", "ERRO na sincronização: ${e.message}", e)
            }
        }
    }
}
