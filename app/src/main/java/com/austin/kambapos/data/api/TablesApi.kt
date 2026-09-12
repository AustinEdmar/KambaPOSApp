package com.austin.kambapos.data.api

import com.austin.kambapos.data.responses.TablesResponse.TablesResponse
import retrofit2.http.GET

interface TablesApi {
    @GET("tables")
     suspend fun getTables(): TablesResponse
}

















