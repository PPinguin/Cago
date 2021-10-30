package com.cago

import android.app.Application
import com.cago.repository.PackController
import com.cago.repository.Repository
import com.cago.repository.database.AppDatabase
import com.cago.repository.managers.FileManager
import com.cago.repository.managers.FirebaseManager

class CagoApplication: Application() {
    
    private val database by lazy{ AppDatabase.getDatabase(applicationContext) }
    private val firebaseManager by lazy{ FirebaseManager(baseContext) }
    private val fileManager by lazy{ FileManager(baseContext) }
    
    val packController by lazy{ 
        PackController(
            baseContext,
            firebaseManager, 
            fileManager
        ) 
    }
    
    val repository by lazy { 
        Repository(
            applicationContext,
            database.packDao(),
            firebaseManager,
            fileManager
        ) 
    }
}