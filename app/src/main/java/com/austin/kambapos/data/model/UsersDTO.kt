package com.austin.kambapos.data.model

data class UsersDTO(
    val id: Int,
    val name: String,
    val email: String,
    val profile_photo: String?,
    val phone: String?,
    val active: Boolean,
    val access_level_id: Int,
    val authorization_barcode: String?,
    val email_verified_at: String?,
    val created_at: String?,
    val updated_at: String?,

    val access_level: AccessLevelDTO
)

data class AccessLevelDTO(
    val id: Int,
    val name: String,
    val created_at: String?,
    val updated_at: String?
)