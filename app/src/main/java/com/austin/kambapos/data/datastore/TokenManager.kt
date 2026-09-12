package com.austin.kambapos.data.datastore

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.austin.kambapos.data.responses.AuthResponses.UserDto
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extension property para criar o DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gson: Gson // injetando Gson
) {

    companion object {
        val TOKEN = stringPreferencesKey("token")
        val AUTH_USER = stringPreferencesKey("auth_user")

        val LOGIN_TIME = longPreferencesKey("login_time")
    }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { prefs ->
            prefs[TOKEN] = token

            Log.d("TokenManager", "Token salvo: $token")

        }
    }



    fun getToken(): Flow<String?> =
        context.dataStore.data.map { preferences ->
            preferences[TOKEN]
        }


    // --- USUÁRIO ---
    suspend fun saveAuthUser(user: UserDto) {
        val json = gson.toJson(user)
        context.dataStore.edit { prefs ->
            prefs[AUTH_USER] = json
        }
    }

    fun getAuthUser(): Flow<UserDto?> =
        context.dataStore.data.map { prefs ->
            prefs[AUTH_USER]?.let { gson.fromJson(it, UserDto::class.java) }
        }


    suspend fun clearToken() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

       // salvar
    suspend fun saveLoginTime() {
        context.dataStore.edit { prefs ->
            prefs[LOGIN_TIME] = System.currentTimeMillis()
        }
    }

    // pegar
    fun isSessionExpired(): Flow<Boolean> =
        context.dataStore.data.map { prefs ->
            val loginTime = prefs[LOGIN_TIME] ?: return@map true
            val eightHours = 8 * 60 * 60 * 1000L
           // val eightHours = 5 * 60 * 1000L
            System.currentTimeMillis() - loginTime > eightHours
        }

}
