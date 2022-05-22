package com.cago.app

import android.app.Application
import com.cago.home.di.HomeComponent
import com.cago.pack.di.PackComponent
import com.cago.core.di.CoreModule
import com.cago.home.di.DaggerHomeComponent
import com.cago.home.di.providers.HomeComponentProvider
import com.cago.pack.di.DaggerPackComponent
import com.cago.pack.di.provider.PackComponentProvider

class CagoApplication: Application(),
    PackComponentProvider,
    HomeComponentProvider{
    
    private val coreModule: CoreModule by lazy {
        CoreModule(this) 
    }
    
    override fun getPackComponent(): PackComponent =
        DaggerPackComponent.builder()
            .coreModule(coreModule)
            .build()

    override fun getHomeComponent(): HomeComponent =
        DaggerHomeComponent.builder()
            .coreModule(coreModule)
            .build()

}