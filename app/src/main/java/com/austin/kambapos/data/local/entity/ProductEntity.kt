package com.austin.kambapos.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "products",
    indices = [Index("categoryId")]
)
data class ProductEntity(
    @PrimaryKey
    val id: Int,

    val name: String,

    val productCode: String,

    val description: String?,

    val price: Double,

    val stock: Int,

    val taxRateId: Int?,

    val taxCode: String?,

    val taxPercentage: Double?,

    val barcode: String?,

    val imageUrl: String?,

    val categoryId: Int
)
