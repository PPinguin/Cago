package com.cago.core.utils

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel(
    val stringProvider: StringProvider
): ViewModel() {
    val message = MutableLiveData<String>()

    fun handleError(error: ErrorType?){
        error?.let{
            message.postValue(stringProvider.get(error.getResource()))
        }
    }
    
    fun message(resId: Int){
        message.postValue(stringProvider.get(resId))
    }
}