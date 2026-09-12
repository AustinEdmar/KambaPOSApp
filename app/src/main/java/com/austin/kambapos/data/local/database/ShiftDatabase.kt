package com.austin.kambapos.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.austin.kambapos.data.local.dao.ShiftDao
import com.austin.kambapos.data.local.entity.ShiftEntity


@Database(
    entities = [ShiftEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ShiftDatabase : RoomDatabase() {

    abstract fun shiftDao(): ShiftDao
}
