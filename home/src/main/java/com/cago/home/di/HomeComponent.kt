package com.cago.home.di

import com.cago.core.di.CoreModule
import com.cago.home.HomeActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        CoreModule::class,
        HomeModule::class
    ]
)
interface HomeComponent {
    fun inject(activity: HomeActivity)
}