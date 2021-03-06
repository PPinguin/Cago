package com.cago.home.di

import com.cago.core.di.CoreModule
import com.cago.home.activities.HomeActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        CoreModule::class,
        RepoModule::class
    ]
)
interface HomeComponent {
    fun inject(activity: HomeActivity)
}