package com.cago.pack.di

import com.cago.core.di.CoreModule
import com.cago.pack.PackActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        CoreModule::class,
        PackModule::class
    ]
)
interface PackComponent {
    fun inject(activity: PackActivity)
}