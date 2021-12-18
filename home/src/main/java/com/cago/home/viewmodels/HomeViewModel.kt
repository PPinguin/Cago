package com.cago.home.viewmodels

import androidx.lifecycle.*
import com.cago.core.models.Pack
import com.cago.core.models.server.PackInfo
import com.cago.core.repository.Repository
import com.cago.core.repository.callbacks.Callback
import com.cago.core.utils.BaseViewModel
import com.cago.core.utils.ErrorType
import com.cago.core.utils.StringProvider
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    stringProvider: StringProvider,
    private val repository: Repository
    ) : BaseViewModel(stringProvider) {

    val listLiveData: LiveData<List<Pack>> = repository.allPacks.asLiveData()
    val searchLiveData = MutableLiveData<List<PackInfo>>(listOf())
    
    lateinit var userInfo: Map<String, String>
    
    fun createPack(name: String) =
        viewModelScope.launch { 
            repository.createPack(name, this@HomeViewModel::handleError) 
        }
    
    fun deletePack(pack: Pack) = 
        viewModelScope.launch {
            repository.deletePack(pack, this@HomeViewModel::handleError)
        }

    fun uploadPack(pack: Pack) = 
        viewModelScope.launch {
            repository.uploadPack(pack, this@HomeViewModel::handleError)
        }

    fun updateSearchResults(query: String) = 
        viewModelScope.launch {
            repository.search(query, object: Callback<List<PackInfo>>{
                override fun success(data: List<PackInfo>?) {
                    searchLiveData.postValue(data!!)
                }
    
                override fun failure(error: ErrorType?) { handleError(error) }
            })
        }
    
    fun generateUri(pack: Pack): String = "http://cago.app/${repository.generatePath(pack)}"
    
    fun initUserInfo(){
        userInfo = repository.getInfo()
    }

    fun isLoggedIn(): Boolean = repository.isLoggedIn()
    
    fun logOut() {
        repository.logOut()
    }
}