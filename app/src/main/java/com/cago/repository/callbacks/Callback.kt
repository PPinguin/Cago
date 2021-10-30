package com.cago.repository.callbacks

import com.cago.utils.ErrorType

interface Callback<T> {
    fun success(data: T? = null)
    fun failure(error: ErrorType? = null)
}