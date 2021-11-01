package com.cago.viewmodels

import androidx.lifecycle.*
import com.cago.models.Pack
import com.cago.models.server.PackInfo
import com.cago.repository.Repository
import com.cago.repository.callbacks.Callback
import com.cago.utils.ErrorType
import kotlinx.coroutines.launch
import java.util.*

class HomeViewModel(private val repository: Repository) : ViewModel() {
    
    class HomeViewModelFactory(private val repository: Repository): ViewModelProvider.Factory{
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(HomeViewModel::class.java)){
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    val listLiveData: LiveData<List<Pack>> = repository.allPacks.asLiveData()
    val searchLiveData = MutableLiveData<List<PackInfo>>(listOf())
    val message = MutableLiveData<String>()
    
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
    
    private fun handleError(error: ErrorType?){
        error?.let{
            message.postValue(repository.context.getString(error.getResource()))
        }
    }
}