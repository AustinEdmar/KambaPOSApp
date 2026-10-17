package com.austin.kambapos.data.api

import com.austin.kambapos.data.model.CreateCustomerRequest
import com.austin.kambapos.data.model.CustomerDTO

import com.austin.kambapos.data.model.LookupRequest
import com.austin.kambapos.data.model.LookupResponse
import com.austin.kambapos.data.model.PagedResponse
import com.austin.kambapos.data.model.UsersDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface CustomerApi {
    @GET("customers")
    suspend fun getCustomers(
        @Query("search") search: String? = null,
        @Query("page") page: Int = 1
    ): PagedResponse<CustomerDTO>

    @POST("customers/lookup")
    suspend fun lookup(@Body body: LookupRequest): Response<LookupResponse>

    @POST("customers")
    suspend fun create(@Body body: CreateCustomerRequest): Response<CustomerDTO>
}