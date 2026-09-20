package com.austin.kambapos.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase

import com.austin.kambapos.data.local.dao.UsersDao
import com.austin.kambapos.data.local.entity.UsersEntity



@Database(
    entities = [UsersEntity::class],
    version = 1,
    exportSchema = false
)
abstract class UsersDatabase : RoomDatabase() {

    abstract fun usersDao(): UsersDao
}
