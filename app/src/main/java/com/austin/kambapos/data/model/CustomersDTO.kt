package com.austin.kambapos.data.model

import com.google.gson.annotations.SerializedName

data class CustomerDTO(
    val id: Long,
    val name: String,
    @SerializedName("tax_number") val taxNumber: String?,
    val phone: String?,
    val address: String?,
    @SerializedName("is_final_consumer") val isFinalConsumer: Boolean
)

data class LookupRequest(@SerializedName("tax_number") val taxNumber: String)

data class LookupResponse(val found: Boolean, val customer: CustomerDTO?)

data class CreateCustomerRequest(
    val name: String,
    @SerializedName("tax_number") val taxNumber: String,
    @SerializedName("customer_type") val customerType: String = "individual",
    val phone: String? = null,
    val email: String? = null,
    val address: String? = null
)

data class PagedResponse<T>(
    val data: List<T>,
    @SerializedName("current_page") val currentPage: Int,
    @SerializedName("last_page") val lastPage: Int
)