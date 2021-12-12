package com.cago.home.viewmodels

import androidx.lifecycle.MutableLiveData
import com.cago.core.repository.Repository
import com.cago.core.repository.callbacks.Callback
import com.cago.core.utils.BaseViewModel
import com.cago.core.utils.ErrorType
import com.cago.core.utils.StringProvider
import javax.inject.Inject

class AuthViewModel @Inject constructor(
    stringProvider: StringProvider,
    private val repository: Repository 
): BaseViewModel(stringProvider) {
    
    val sendLiveData = MutableLiveData(false)
    val logLiveData = MutableLiveData(false)

    fun sendLink(email:String){
        repository.sendLink(email, object : Callback<Nothing> {
            override fun success(data: Nothing?) {
                sendLiveData.postValue(true)
            }

            override fun failure(error: ErrorType?) {
                handleError(error) 
            }
        })
    }
    
    fun isLoggedIn(): Boolean {
        logLiveData.postValue(repository.isLoggedIn())
        return logLiveData.value ?: false
    }

    fun logIn(link: String){
        repository.logIn(link, object : Callback<Nothing>{
            override fun success(data: Nothing?) {
                logLiveData.postValue(true)
            }

            override fun failure(error: ErrorType?) { handleError(error) }

        })
    }
}