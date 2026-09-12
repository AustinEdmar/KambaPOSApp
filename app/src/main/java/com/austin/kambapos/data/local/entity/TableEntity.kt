package com.austin.kambapos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tables")
data class TableEntity(
    @PrimaryKey val id: Int,
    val number: String,
    val status: String
)
