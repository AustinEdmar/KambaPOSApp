package com.austin.kambapos.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.austin.kambapos.data.local.entity.TableEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TableDao {

    @Query("SELECT * FROM tables ORDER BY id")
    fun observeTables(): Flow<List<TableEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTables(tables: List<TableEntity>)

    @Query("DELETE FROM tables")
    suspend fun clearTables()

    @Query("SELECT * FROM tables")
    suspend fun getTablesOnce(): List<TableEntity>


    @Query("DELETE FROM tables WHERE id IN (:ids)")
    suspend fun deleteTablesByIds(ids: List<Int>)

}
