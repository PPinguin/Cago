package com.cago.home.viewmodels

import android.net.Uri
import androidx.lifecycle.*
import com.cago.core.models.Pack
import com.cago.core.repository.Repository
import com.cago.core.utils.BaseViewModel
import com.cago.core.utils.StringProvider
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

class HomeViewModel @Inject constructor(
    stringProvider: StringProvider,
    private val repository: Repository
    ) : BaseViewModel(stringProvider) {

    val listLiveData: LiveData<List<Pack>> = repository.allPacks.asLiveData()
    
    fun createPack(name: String, source: Uri? = null) =
        viewModelScope.launch { 
            repository.createPack(name, source, this@HomeViewModel::handleError) 
        }
    
    fun deletePack(pack: Pack) = 
        viewModelScope.launch {
            repository.deletePack(pack, this@HomeViewModel::handleError)
        }
    
    fun getPackUri(pack: Pack) = repository.getPackUri(pack)
}