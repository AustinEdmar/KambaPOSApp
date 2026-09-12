package com.austin.kambapos.data.api

import com.austin.kambapos.data.model.ShiftRequest.ShiftRequest
import com.austin.kambapos.data.responses.ShiftsResponses.ShiftResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ShiftApi {

    @POST("shifts/open")
    suspend fun open(
        @Body body: ShiftRequest
    ): ShiftResponse

    @POST("shifts/close")
    suspend fun close(
        @Body body: ShiftRequest
    ): ShiftResponse


    @GET("shifts/current") 
    suspend fun shiftCurrent(): ShiftResponse
}
