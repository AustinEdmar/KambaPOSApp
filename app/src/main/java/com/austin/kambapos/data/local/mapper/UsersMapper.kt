package com.austin.kambapos.data.local.mapper

import com.austin.kambapos.data.local.entity.UsersEntity
import com.austin.kambapos.data.model.UsersDTO

fun UsersDTO.toEntity(): UsersEntity {
    return UsersEntity(
        id = id,
        name = name,
        email = email,
        profilePhoto = profile_photo,
        phone = phone,
        active = active,
        accessLevelId = access_level_id,
        authorizationBarcode = authorization_barcode,
        emailVerifiedAt = email_verified_at,
        createdAt = created_at,
        updatedAt = updated_at
    )
}