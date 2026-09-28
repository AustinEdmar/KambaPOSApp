package com.austin.kambapos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.austin.kambapos.data.local.entity.UsersEntity

@Dao
interface UsersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UsersEntity>)

    @Query("SELECT * FROM users WHERE authorizationPin = :pin AND active = 1 LIMIT 1")
    suspend fun findByPin(pin: String): UsersEntity?

    @Query("DELETE FROM users")
    suspend fun clearAll()
}