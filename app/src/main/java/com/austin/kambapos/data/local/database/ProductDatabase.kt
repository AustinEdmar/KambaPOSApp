package com.austin.kambapos.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.austin.kambapos.data.local.dao.CategoryDao
import com.austin.kambapos.data.local.dao.ProductDao
import com.austin.kambapos.data.local.entity.CategoryEntity
import com.austin.kambapos.data.local.entity.ProductEntity


@Database(
    entities = [ProductEntity::class, CategoryEntity::class],
    version = 2
)
abstract class ProductDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun categoryDao(): CategoryDao
}
