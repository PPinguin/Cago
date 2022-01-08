package com.cago.core.repository.callbacks

import com.cago.core.utils.ErrorType

interface Callback<T> {
    fun success(data: T? = null)
    fun failure(error: ErrorType? = null)
}