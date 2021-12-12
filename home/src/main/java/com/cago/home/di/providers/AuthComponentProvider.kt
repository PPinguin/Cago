package com.cago.home.di.providers

import com.cago.home.di.AuthComponent

interface AuthComponentProvider {
    fun getAuthComponent(): AuthComponent
}