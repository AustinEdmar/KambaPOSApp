package com.austin.kambapos.data.api

import com.austin.kambapos.data.model.UsersDTO
import retrofit2.http.GET

interface UsersApi {
    @GET("users")
    suspend fun getUsers(): List<UsersDTO>
}