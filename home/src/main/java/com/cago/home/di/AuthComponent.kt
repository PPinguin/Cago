package com.cago.home.di

import com.cago.core.di.CoreModule
import com.cago.home.activities.AuthActivity
import com.cago.home.di.RepoModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        CoreModule::class,
        RepoModule::class
    ]
)
interface AuthComponent {
    fun inject(activity: AuthActivity)
}