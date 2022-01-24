package com.cago.core.repository.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.cago.core.models.Pack

@Database(
    version = 2, 
    entities = [Pack::class]
)
abstract class 
AppDatabase: RoomDatabase() {
    
    abstract fun packDao(): PackDao
    
    companion object{
        @Volatile
        private var database: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return database ?: synchronized(this){
                val db = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "packages-database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                database = db
                db
            }
        }
    }
}