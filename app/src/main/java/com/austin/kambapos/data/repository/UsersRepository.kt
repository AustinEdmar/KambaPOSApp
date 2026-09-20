package com.austin.kambapos.data.repository

import android.util.Log
import com.austin.kambapos.data.api.UsersApi
import com.austin.kambapos.data.local.dao.UsersDao
import com.austin.kambapos.data.local.entity.UsersEntity
import com.austin.kambapos.data.local.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UsersRepository @Inject constructor (
    private val api: UsersApi,
    private val usersDao: UsersDao
){

    suspend fun syncUsers() {
        try {
            Log.d("UsersRepository", "=== INICIANDO SYNC DE USUÁRIOS ===")
            val response = api.getUsers()
            
            // Logando a quantidade e os nomes dos usuários recebidos
            Log.d("UsersRepository", "Recebidos ${response.size} usuários da API")
            response.forEach { user ->
                Log.d("UsersRepository", "Usuário: ID=${user.id}, Nome=${user.name}, Email=${user.email}")
            }

            usersDao.insertUsers(response.map { it.toEntity() })
            Log.d("UsersRepository", "Dados salvos no Banco de Dados com sucesso")
            
        } catch (e: Exception) {
            Log.e("UsersRepository", "ERRO CRÍTICO NA SINCRONIZAÇÃO: ${e.message}", e)
        }
    }

    fun getLocalUsers(): Flow<List<UsersEntity>> {
        return usersDao.getAllUsers()
    }
}
