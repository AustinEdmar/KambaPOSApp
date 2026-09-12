package com.austin.kambapos.data.api

import com.austin.kambapos.data.model.CategoryDTO
import com.austin.kambapos.data.model.ProductDTO
import com.austin.kambapos.data.responses.ProductsResponse.ApiResponse
import retrofit2.http.GET
import retrofit2.http.Query


interface ProductApi {

    @GET("products")
    suspend fun getProducts(
        @Query("category_id") categoryId: Int? = null,
        @Query("search") search: String? = null
    ): ApiResponse<ProductDTO>


    @GET("categories")
    suspend fun getCategories(): ApiResponse<CategoryDTO>
}
