package com.cago.repository.managers

import android.content.Context
import com.cago.repository.Repository
import java.io.File

class FileManager(override val context: Context): Manager {
    private var dir: File? = context.getExternalFilesDir(null)
    
    fun getFile(name: String): File = File(dir, "$name.cg")
    
    fun valid(name: String): Boolean = 
        with(getFile(name)){
            exists() && isFile
        }

    fun createPack(name: String): Boolean {
        val f = getFile(name)
        return f.createNewFile().also { if (it) f.appendText("${Repository.UID}\n") }
    }

    fun deletePack(name: String): Boolean =
        getFile(name).delete()
}