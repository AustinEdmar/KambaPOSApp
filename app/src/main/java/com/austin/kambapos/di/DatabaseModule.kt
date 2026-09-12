package com.austin.kambapos.di

import android.app.Application
import androidx.room.Room
import com.austin.kambapos.data.local.dao.CartDao
import com.austin.kambapos.data.local.dao.CategoryDao
import com.austin.kambapos.data.local.dao.OrderDao
import com.austin.kambapos.data.local.dao.ProductDao
import com.austin.kambapos.data.local.dao.ShiftDao
import com.austin.kambapos.data.local.dao.TableDao
import com.austin.kambapos.data.local.database.OrderDatabase
import com.austin.kambapos.data.local.database.ProductDatabase
import com.austin.kambapos.data.local.database.ShiftDatabase
import com.austin.kambapos.data.local.database.TablesDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideTaskDatabase(context: Application) : TablesDatabase{
        return Room.databaseBuilder(
            context,
            TablesDatabase::class.java,
            "table_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideTasksDao(db: TablesDatabase): TableDao {
        return db.TableDao()
    }

    @Provides
    @Singleton
    fun provideShiftDatabase(context: Application) : ShiftDatabase {
        return Room.databaseBuilder(
            context,
            ShiftDatabase::class.java,
            "shift_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideShiftDao(db: ShiftDatabase): ShiftDao {
        return db.shiftDao()
    }

    @Provides
    @Singleton
    fun provideProductDatabase(
        context: Application
    ): ProductDatabase {
        return Room.databaseBuilder(
            context,
            ProductDatabase::class.java,
            "product_db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideProductDao(
        db: ProductDatabase
    ): ProductDao {
        return db.productDao()
    }

    @Provides
    @Singleton
    fun provideCategoryDao(
        db: ProductDatabase
    ): CategoryDao {
        return db.categoryDao()
    }

   @Provides
   @Singleton
   fun provideOrderDatabase(context: Application) : OrderDatabase {
       return Room.databaseBuilder(
           context,
           OrderDatabase::class.java,
           "order_db"
       )
           .fallbackToDestructiveMigration()
           .build()
   }

    @Provides
    @Singleton
    fun provideOrderDao(db: OrderDatabase): OrderDao {
        return db.OrderDao()
    }

    @Provides
    @Singleton
    fun provideCartDao(db: OrderDatabase): CartDao {
        return db.CartDao()
    }

}