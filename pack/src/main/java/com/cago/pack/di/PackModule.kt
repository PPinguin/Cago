package com.cago.pack.di

import com.cago.core.repository.PackController
import com.cago.core.repository.managers.FileManager
import com.cago.core.repository.managers.FirebaseManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class PackModule {
    @Provides
    @Singleton
    fun packController(
        firebaseManager: FirebaseManager,
        fileManager: FileManager
    ): PackController = PackController(firebaseManager, fileManager)
}