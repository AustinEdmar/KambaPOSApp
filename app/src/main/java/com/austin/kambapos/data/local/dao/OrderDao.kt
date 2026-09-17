package com.austin.kambapos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.austin.kambapos.data.local.entity.OrderEntity
import com.austin.kambapos.data.local.entity.OrderItemEntity
import com.austin.kambapos.data.local.entity.OrderWithItems
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(order: OrderEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrders(orders: List<OrderEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<OrderItemEntity>)

    @Query("DELETE FROM order_items WHERE order_id = :orderId")
    suspend fun clearItemsForOrder(orderId: Int)

    @Transaction
    @Query("SELECT * FROM orders WHERE id = :orderId")
    fun observeOrderWithItems(orderId: Int): Flow<OrderWithItems?>

    @Transaction
    @Query("SELECT * FROM orders WHERE user_id = :userId")
    fun observeOrdersWithItems(userId: Int): Flow<List<OrderWithItems>>

    @Query("SELECT * FROM orders WHERE status = 'open' LIMIT 1")
    fun observeOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders WHERE user_id = :user_id")
    fun getOrdersByUser(user_id: Int): Flow<List<OrderEntity>>

    @Query("SELECT * FROM orders")
    suspend fun getOrdersOnce(): List<OrderEntity>

    @Query("DELETE FROM orders WHERE id IN (:ids)")
    suspend fun deleteOrdersByIds(ids: List<Int>)

    @Query("DELETE FROM orders WHERE id = :id")
    suspend fun deleteOrderById(id: Int?)

    @Query("DELETE FROM orders WHERE user_id = :userId")
    suspend fun deleteOrdersByUserId(userId: Int)

    @Query("DELETE FROM orders")
    suspend fun clearOrders()
}