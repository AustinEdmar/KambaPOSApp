package com.austin.kambapos.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.austin.kambapos.data.local.dao.CartDao
import com.austin.kambapos.data.local.dao.OrderDao
import com.austin.kambapos.data.local.entity.CartItemEntity
import com.austin.kambapos.data.local.entity.OrderEntity
import com.austin.kambapos.data.local.entity.ProductEntity


@Database(
    entities = [OrderEntity::class, CartItemEntity::class, ProductEntity::class],
    version = 4,
    exportSchema = false
)
abstract class OrderDatabase : RoomDatabase() {

    abstract fun OrderDao(): OrderDao
    abstract fun CartDao(): CartDao
}
