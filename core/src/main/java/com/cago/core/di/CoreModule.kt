package com.cago.core.di

import android.app.Application
import com.cago.core.repository.database.AppDatabase
import com.cago.core.repository.database.PackDao
import com.cago.core.repository.managers.FileManager
import com.cago.core.repository.managers.FirebaseManager
import com.cago.core.utils.StringProvider
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class CoreModule(
    private val application: Application
) {
    @Provides
    @Singleton
    fun stringProvider(): StringProvider = StringProvider(application)
    
    @Provides
    @Singleton
    fun firebaseManager(): FirebaseManager = FirebaseManager(application)

    @Provides
    @Singleton
    fun fileManager(): FileManager = FileManager(application)
    
    @Provides
    @Singleton
    fun provideDao(): PackDao
            = AppDatabase.getDatabase(application).packDao()
}