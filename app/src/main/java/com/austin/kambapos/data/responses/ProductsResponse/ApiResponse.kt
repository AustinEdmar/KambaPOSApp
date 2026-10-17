package com.austin.kambapos.data.responses.ProductsResponse

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    val data: List<T>,
    @SerializedName("current_page") val currentPage: Int = 1,
    @SerializedName("last_page") val lastPage: Int = 1,
)
