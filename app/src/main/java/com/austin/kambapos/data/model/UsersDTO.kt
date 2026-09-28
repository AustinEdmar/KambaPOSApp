package com.austin.kambapos.data.model

data class UsersDTO(
    val id: Int,
    val name: String,
    val email: String,
    val authorization_pin: String?,
    val active: Boolean,
    val access_level_id: Int,
    val access_level: AccessLevelDTO
)

data class AccessLevelDTO(
    val id: Int,
    val name: String
)