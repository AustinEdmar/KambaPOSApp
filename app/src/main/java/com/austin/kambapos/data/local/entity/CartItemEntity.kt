package com.austin.kambapos.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(
    tableName = "cart_items",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("productId")]
)
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val orderId: Int?,
    val productId: Int,
    val pendingAuthorizationPin: String? = null,
    val quantity: Int = 1,
    val delta: Int = 0,

    val pendingSync: Boolean = true,
    val syncVersion: Int = 0,  // ← novo campo

    // NOVO: congelados no momento do addToCartTransaction — nunca mudam
    // depois, mesmo que o preço do produto no catálogo seja actualizado.
    // Espelha unit_price/iva_rate do order_items na API.
    val unitPriceSnapshot: Double = 0.0,
    val ivaRateSnapshot: Double = 0.0
)

