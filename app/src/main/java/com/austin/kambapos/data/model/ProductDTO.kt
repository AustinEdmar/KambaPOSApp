package com.austin.kambapos.data.model

import com.google.gson.annotations.SerializedName

data class ProductDTO(
    val id: Int,
    val name: String,
    @SerializedName("product_code")
    val productCode: String,
    val description: String?,
    val price: Double,
    val unit: String,

    @SerializedName("tax_rate_id")
    val taxRateId: Int?,

    @SerializedName("tax_exemption_reason")
    val taxExemptionReason: String?,

    val stock: Int,
    val barcode: String?,

    @SerializedName("category_id")
    val categoryId: Int,

    @SerializedName("image_path")
    val imagePath: String?,

    @SerializedName("is_active")
    val isActive: Boolean,

    @SerializedName("category")
    val categoryDTO: CategoryDTO,

    @SerializedName("tax_rate")
    val taxRateDTO: TaxRateDTO?
)
