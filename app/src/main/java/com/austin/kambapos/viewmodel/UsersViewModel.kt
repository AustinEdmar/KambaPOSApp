package com.austin.kambapos.viewmodel


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.austin.kambapos.data.repository.UsersRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsersViewModel @Inject constructor(
    private val repository: UsersRepository
) : ViewModel() {

    fun syncUsers() {
        viewModelScope.launch {
            try {
                repository.syncUsers()
            } catch (e: Exception) {
                Log.e("UsersViewModel", "Erro ao sincronizar users", e)
            }
        }
    }
}