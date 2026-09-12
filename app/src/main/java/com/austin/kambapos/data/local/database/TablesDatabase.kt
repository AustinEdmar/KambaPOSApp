package com.austin.kambapos.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.austin.kambapos.data.local.dao.TableDao
import com.austin.kambapos.data.local.entity.TableEntity

@Database(
    entities = [TableEntity::class],
    version = 1,
    exportSchema = false
)


// Classe abstrata que estende RoomDatabase
abstract class TablesDatabase: RoomDatabase(){
    // Função abstrata que retorna um objeto do tipo TasksDao
    abstract fun TableDao(): TableDao
}