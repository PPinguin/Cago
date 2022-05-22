package com.cago.core.repository

import android.net.Uri
import androidx.core.net.toUri
import com.cago.core.models.Pack
import com.cago.core.repository.database.PackDao
import com.cago.core.repository.managers.FileManager
import com.cago.core.utils.ErrorType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Singleton
class Repository(
    private val packDao: PackDao,
    private val fileManager: FileManager
){
    val allPacks: Flow<List<Pack>> = packDao.getAllFlow()  
    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        scope.launch {
            packDao.getList().forEach { 
                if(!fileManager.valid(it.name, it.path)) {
                    fileManager.deletePack(it.name, it.path)
                    packDao.delete(it)
                }
            }
        }
    }
    
    fun createPack(name: String, source: Uri?, handle: (ErrorType?) -> Unit){
        if(fileManager.createPack(name))
            if(source != null){
                if(fileManager.copy(source, name))
                    scope.launch {
                        packDao.insert(Pack(name))
                    }
                else handle(ErrorType.ERROR_CREATE)
            } else 
                scope.launch {
                    packDao.insert(Pack(name))
                }
        else handle(ErrorType.ERROR_CREATE)
    }
    
    fun deletePack(pack: Pack, handle: (ErrorType?) -> Unit) {
        if(fileManager.deletePack(pack.name, pack.path)) {
            scope.launch { 
                packDao.delete(pack)
            }
        }
        else handle(ErrorType.ERROR_DELETE)
    }
    
    fun getPackUri(pack: Pack) = fileManager.getFile(pack.name).toUri()
}