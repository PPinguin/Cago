package com.cago.repository.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.cago.models.Pack

@Database(entities = [Pack::class], version = 1, exportSchema = false)
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
                ).build()
                database = db
                db
            }
        }
    }
}