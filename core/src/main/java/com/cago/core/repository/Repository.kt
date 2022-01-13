package com.cago.core.repository

import com.cago.core.models.Pack
import com.cago.core.models.server.PackInfo
import com.cago.core.repository.callbacks.Callback
import com.cago.core.repository.database.PackDao
import com.cago.core.repository.managers.FileManager
import com.cago.core.repository.managers.FirebaseManager
import com.cago.core.utils.ErrorType
import com.cago.core.utils.GlobalUtils.UID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class Repository(
    val packDao: PackDao,
    private val firebaseManager: FirebaseManager,
    private val fileManager: FileManager
){
    val allPacks: Flow<List<Pack>> = packDao.getAllFlow()  
    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        UID = firebaseManager.getCurrentUID()
        scope.launch {
            packDao.getList().forEach { 
                if(!fileManager.valid(it.name)) {
                    fileManager.deletePack(it.name)
                    packDao.delete(it)
                }
            }
        }
    }
    
    fun synchronizePackages(){
        firebaseManager.syncPackages { name, uid ->
            if (fileManager.createPack(name)) {
                scope.launch { packDao.insert(Pack(name, uid)) }
                fileManager.getFile(name)
            } else null
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
    
    fun isLoggedIn(): Boolean = firebaseManager.isLoggedIn()
    
    fun logIn(link: String, callback: Callback<Nothing>){
        firebaseManager.logIn(link, callback)
        synchronizePackages()
    }
    
    fun sendLink(email: String, callback: Callback<Nothing>){
        firebaseManager.sendLink(email, callback)
    }
    
    fun logOut(){
        scope.launch {
            packDao.getList().forEach {                 
                fileManager.deletePack(it.name)
                packDao.delete(it)
            }
        }
        firebaseManager.logOut() 
    }
    
    fun getInfo() = firebaseManager.getCurrentInfo() ?: emptyMap()
}