package com.austin.kambapos.data.api

import com.austin.kambapos.data.model.OrderResquest.AddItemRequest
import com.austin.kambapos.data.model.OrderResquest.CloseOrderRequest
import com.austin.kambapos.data.model.OrderResquest.OpenOrderRequest
import com.austin.kambapos.data.model.OrdersDTO
import com.austin.kambapos.data.responses.OrderResponses.CloseOrderResponse
import com.austin.kambapos.data.responses.OrderResponses.OrdersResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface OrdersApi {

    @POST("orders/open")
    suspend fun getOrder(
        @Body request: OpenOrderRequest
    ): OrdersDTO



//    @GET("orders")
//    suspend fun getOrders(): List<OrdersDTO>

    @GET("orders")
    suspend fun getOrders(): OrdersResponse


    @POST("orders/{orderId}/add-item")
    suspend fun addItem(
        @Path("orderId") orderId: Int?,
        @Body request: AddItemRequest
    ): Response<ResponseBody>



    @POST("orders/{orderId}/decrement-item")
    suspend fun decrementItem(
        @Path("orderId") orderId: Int?,
        @Body request: AddItemRequest
    ): Response<ResponseBody>


    @POST("orders/{orderId}/close")
    suspend fun closeOrder(
        @Path("orderId") orderId: Int?,
        @Body request: CloseOrderRequest
    ): Response<CloseOrderResponse>



}