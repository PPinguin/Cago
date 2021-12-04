package com.cago.pack.di.provider

import com.cago.pack.di.PackComponent

interface PackComponentProvider {
    fun getPackComponent(): PackComponent
}