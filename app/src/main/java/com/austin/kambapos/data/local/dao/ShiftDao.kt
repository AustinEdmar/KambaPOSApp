package com.austin.kambapos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.austin.kambapos.data.local.entity.ShiftEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ShiftDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(shift: ShiftEntity)

    @Query("DELETE FROM shifts WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM shifts LIMIT 1")
    suspend fun getShiftOnce(): ShiftEntity?

    @Update
    suspend fun update(shift: ShiftEntity)


    @Query("DELETE FROM shifts")
    suspend fun clearAll()

    @Query("SELECT * FROM shifts LIMIT 1")
    suspend fun getShift(): ShiftEntity?

    @Query("SELECT * FROM shifts LIMIT 1")
    fun observeShift(): Flow<ShiftEntity?>
}
