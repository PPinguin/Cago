package com.cago.home.di

import com.cago.core.repository.Repository
import com.cago.core.repository.database.PackDao
import com.cago.core.repository.managers.FileManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RepoModule {
    @Provides
    @Singleton
    fun repository(
        dao: PackDao,
        fileManager: FileManager
    ): Repository =
        Repository(dao, fileManager)
}