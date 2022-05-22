package com.cago.pack.di

import com.cago.core.repository.PackController
import com.cago.core.repository.managers.FileManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class PackModule {
    @Provides
    @Singleton
    fun packController(
        fileManager: FileManager
    ): PackController = PackController(fileManager)
}