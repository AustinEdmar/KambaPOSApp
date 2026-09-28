package com.austin.kambapos.data.local.mapper

import com.austin.kambapos.data.local.entity.UsersEntity
import com.austin.kambapos.data.model.UsersDTO

fun UsersDTO.toEntity(): UsersEntity {
    return UsersEntity(
        id = id,
        name = name,
        email = email,
        authorizationPin = authorization_pin,
        active = active,
        accessLevelId = access_level_id,
        accessLevelName = access_level.name
    )
}