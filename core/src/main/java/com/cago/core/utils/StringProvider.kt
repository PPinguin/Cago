package com.cago.core.utils

import android.content.Context

class StringProvider(val context: Context){
    fun get(res: Int) = context.getString(res)
}