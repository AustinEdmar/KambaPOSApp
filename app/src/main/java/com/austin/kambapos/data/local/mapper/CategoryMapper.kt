package com.austin.kambapos.data.local.mapper

import com.austin.kambapos.data.local.entity.CategoryEntity
import com.austin.kambapos.data.model.CategoryDTO

fun CategoryDTO.toEntity(): CategoryEntity {
    return CategoryEntity(
        id = id,
        name = name,
        imagePath = imagePath,
        created_at = created_at,
        updated_at = updated_at
    )
}
