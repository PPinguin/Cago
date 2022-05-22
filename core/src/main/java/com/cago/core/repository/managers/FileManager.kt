package com.cago.core.repository.managers

import android.content.Context
import android.net.Uri
import androidx.core.net.toFile
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.channels.FileChannel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileManager @Inject constructor(override val context: Context): Manager {
    private var dir: File? = context.getExternalFilesDir(null)
    
    fun getFile(name: String, path: String? = null): File = 
        if(path == null) File(dir, "$name.cg")
        else File(dir?.absolutePath + "/$path/" + "$name.cg")
    
    fun valid(name: String, path: String? = null): Boolean = 
        try {
            with(getFile(name, path)){
                    exists() && isFile
            }
        } catch (e: Exception){
            false
        }

    fun createPack(name: String): Boolean {
        val f = getFile(name)
        return f.createNewFile()
    }

    fun deletePack(name: String, path: String? = null): Boolean =
        getFile(name, path).delete()
    
    fun copy(source: Uri, name: String): Boolean {
        val from: FileChannel = FileInputStream(source.toFile()).channel
        val to: FileChannel = FileOutputStream(getFile(name)).channel
        return try{
            from.transferTo(0, from.size(), to)
            true
        } catch (e: Exception){
            false
        } finally {
            from.close()
            to.close()
        }
    }
    
    fun cache(source: Uri, name: String): File? {
        val file = getFile(name, "cache")
        
        return try{
            context.contentResolver.openInputStream(source)?.use { from ->
                FileOutputStream(file).use{ to ->
                    val buf = ByteArray(1024)
                    var len: Int
                    while (from.read(buf).also { len = it } > 0) {
                        to.write(buf, 0, len)
                    }
                }
            }
            file
        } catch (e: Exception){
            null
        }
    }
}