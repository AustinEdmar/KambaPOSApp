package com.austin.kambapos.data.local.mapper

import com.austin.kambapos.data.local.entity.OrderEntity
import com.austin.kambapos.data.local.entity.OrderItemEntity
import com.austin.kambapos.data.model.OrderItemDTO
import com.austin.kambapos.data.model.OrdersDTO
import kotlin.text.toDoubleOrNull


// mapper/OrderMapper.kt

fun OrdersDTO.toEntity() = OrderEntity(
    id = id,
    user_id = user_id,
    shift_id = shift_id,
    status = status,
    subtotal = subtotal,
    iva = iva,
    discount = discount,
    total = total,
    opened_at = opened_at,
    updated_at = updated_at,
    created_at = created_at
)

fun OrderItemDTO.toEntity(orderId: Int) = OrderItemEntity(
    id = id,
    order_id = orderId,
    product_id = product_id,
    product_name = product_name,
    product_code = product_code,
    unit = unit,
    quantity = quantity,
    unit_price = unit_price,
    discount_percent = discount_percent,
    discount_amount = discount_amount,
    iva_rate = iva_rate,
    tax_code = tax_code,
    iva_amount = iva_amount,
    subtotal = subtotal,
    total_with_iva = total_with_iva,
    status = status,
    product_name_local = product?.name,
    product_image_path = product?.image_path
)