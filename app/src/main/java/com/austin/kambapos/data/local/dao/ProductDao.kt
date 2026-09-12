package com.austin.kambapos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.austin.kambapos.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Query("""
        SELECT * FROM products
        WHERE (:categoryId IS NULL OR categoryId = :categoryId)
        AND (:search IS NULL OR name LIKE '%' || :search || '%')
    """)
    fun observeProducts(
        categoryId: Int?,
        search: String?
    ): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertAll(products: List<ProductEntity>)





    @Query("DELETE FROM products")
    suspend fun clearAll()
}