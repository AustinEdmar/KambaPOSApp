package com.austin.kambapos.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "users")
data class UsersEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val email: String,
    val profilePhoto: String?,
    val phone: String?,
    val active: Boolean,
    val accessLevelId: Int,
    val authorizationBarcode: String?,
    val emailVerifiedAt: String?,
    val createdAt: String?,
    val updatedAt: String?
)
