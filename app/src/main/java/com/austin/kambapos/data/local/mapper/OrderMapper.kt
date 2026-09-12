package com.austin.kambapos.data.local.mapper

import com.austin.kambapos.data.local.entity.OrderEntity
import com.austin.kambapos.data.model.OrdersDTO

fun OrdersDTO.toEntity() = OrderEntity(
    id = id,
    user_id = user_id,
    shift_id = shift_id,

    status = status,
    subtotal = subtotal,
    total = total,
    opened_at = opened_at,
    updated_at = updated_at,
    created_at = created_at
)
