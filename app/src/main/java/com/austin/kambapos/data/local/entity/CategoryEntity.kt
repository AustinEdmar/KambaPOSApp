package com.austin.kambapos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val imagePath: String?,
    val created_at: String?,
    val updated_at: String?
)
