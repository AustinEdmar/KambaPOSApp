package com.austin.kambapos.data.model

import com.google.gson.annotations.SerializedName

data class CategoryDTO(
    val id: Int,
    val name: String,
    @SerializedName("image_path")
    val imagePath: String? = null,
    val created_at: String? = null,
    val updated_at: String? = null
)
