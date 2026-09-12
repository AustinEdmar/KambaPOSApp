package com.austin.kambapos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.austin.kambapos.data.local.entity.OrderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(order: OrderEntity)

    @Query("SELECT * FROM orders WHERE status = 'open' LIMIT 1")
    fun observeOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE user_id = :user_id")
    fun getOrdersByUser(user_id: Int): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders")
    suspend fun getOrdersOnce(): List<OrderEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrders(orders: List<OrderEntity>)

    @Query("DELETE FROM orders WHERE id IN (:ids)")
    suspend fun deleteOrdersByIds(ids: List<Int>)

    @Query("DELETE FROM orders WHERE id = :id")
    suspend fun deleteOrderById(id: Int?)

    @Query("DELETE FROM orders WHERE user_id = :userId")
    suspend fun deleteOrdersByUserId(userId: Int)

    @Query("DELETE FROM orders")
    suspend fun clearOrders()
}
