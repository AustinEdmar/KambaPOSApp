package com.austin.kambapos.data.repository

import android.util.Log
import com.austin.kambapos.data.api.AuthApi
import com.austin.kambapos.data.datastore.TokenManager
import com.austin.kambapos.data.responses.AuthResponses.AuthResponse
import com.austin.kambapos.data.model.AuthRequest.LoginRequest
import com.austin.kambapos.data.responses.AuthResponses.UserDto
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthRepository @Inject constructor(
    private val api: AuthApi,
    private val tokenManager: TokenManager
) {

    /**
     * LOGIN
     * - chama

     * - salva token
     * - salva usuário
     */
    suspend fun login(email: String, password: String): AuthResponse {
        val response = api.login(LoginRequest(email, password))

        Log.d("AuthRepository", "Login response: $response")

        tokenManager.saveToken(response.accessToken)
        tokenManager.saveAuthUser(response.user)
        tokenManager.saveLoginTime()

        return response
    }

    /**
     * USUÁRIO LOCAL (DataStore)
     * - rápido
     * - offline
     */
    fun getAuthenticatedUserLocal(): Flow<UserDto?> {
        return tokenManager.getAuthUser()
    }

    /**
     * USUÁRIO REMOTO (API /me)
     * - sempre atualizado
     * - atualiza o DataStore
     */
    suspend fun refreshAuthenticatedUser(): UserDto {
        val user = api.me().user
        tokenManager.saveAuthUser(user)
        return user
    }

    /**
     * VERIFICA SE EXISTE TOKEN
     * - usado para splash / auth guard
     */
    fun hasToken(): Flow<Boolean> =
        tokenManager.getToken().map { !it.isNullOrEmpty() }

    /**
     * LOGOUT
     * - limpa token
     * - limpa usuário
     */


    suspend fun logout() {
        try {
            api.logout() // POST /logout
        } catch (e: Exception) {
            // ignora erro (token pode estar expirado)
        } finally {
            tokenManager.clearToken()
        }
    }


    fun shouldLogout(): Flow<Boolean> =
        tokenManager.isSessionExpired()

}
