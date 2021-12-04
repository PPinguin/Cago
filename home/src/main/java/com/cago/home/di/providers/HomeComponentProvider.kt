package com.cago.home.di.providers

import com.cago.home.di.HomeComponent

interface HomeComponentProvider {
    fun getHomeComponent(): HomeComponent
}