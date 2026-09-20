package com.austin.kambapos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.austin.kambapos.data.local.entity.UsersEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UsersDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UsersEntity>)

    @Query("SELECT * FROM users WHERE active = 1")
    fun getAllUsers(): Flow<List<UsersEntity>>
}