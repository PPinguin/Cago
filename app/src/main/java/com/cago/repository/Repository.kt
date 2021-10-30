package com.cago.repository

import android.content.Context
import androidx.core.content.edit
import com.cago.models.Pack
import com.cago.models.server.PackInfo
import com.cago.repository.callbacks.Callback
import com.cago.repository.database.PackDao
import com.cago.repository.managers.FileManager
import com.cago.repository.managers.FirebaseManager
import com.cago.repository.managers.Manager
import com.cago.utils.ErrorType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class Repository(
    override val context: Context,
    val packDao: PackDao,
    private val firebaseManager: FirebaseManager,
    private val fileManager: FileManager
) : Manager {
    private val key = "ID"
    companion object{
        var UID: String? = null
    }
    
    val allPacks: Flow<List<Pack>> = packDao.getAllFlow()  
    private val scope = CoroutineScope(Job())

    init {
        val sharedPref = context.getSharedPreferences(key, Context.MODE_PRIVATE)
        UID = sharedPref.getString(key, null)
        if (UID == null) {
            sharedPref.edit {
                putString(key, firebaseManager.getCurrentUID())
            }
        }
        UID = sharedPref.getString(key, null)
        scope.launch {
            packDao.getList().forEach { 
                if(!fileManager.valid(it.name)) {
                    fileManager.deletePack(it.name)
                    packDao.delete(it)
                }
            }
        }
    }
    
    fun createPack(name: String, handle: (ErrorType?) -> Unit){
        if(fileManager.createPack(name))
            scope.launch {
                packDao.insert(Pack(name, null))
            }
        else handle(ErrorType.ERROR_CREATE)
    }
    
    fun deletePack(pack: Pack, handle: (ErrorType?) -> Unit) {
        if(fileManager.deletePack(pack.name)) {
            scope.launch { 
                firebaseManager.delete(pack)
                packDao.delete(pack)
            }
        }
        else handle(ErrorType.ERROR_DELETE)
    }
    
    fun search(query: String, callback: Callback<List<PackInfo>>) {
        firebaseManager.searchByQuery(query, callback)
    }
    
    fun generatePath(pack: Pack) = "$UID/${pack.name}"

    fun uploadPack(pack: Pack, handle: (ErrorType?) -> Unit) {
        firebaseManager.uploadPack(fileManager.getFile(pack.name), object : Callback<String> {
            override fun success(data: String?) {
                scope.launch { 
                    packDao.update(pack.also { it.key = data })
                }
            }
            override fun failure(error: ErrorType?) {
                handle(error)
            }
        })
    }
}