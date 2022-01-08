package com.cago.core.repository.managers

import android.content.Context
import com.cago.core.utils.GlobalUtils.UID
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileManager @Inject constructor(override val context: Context): Manager {
    private var dir: File? = context.getExternalFilesDir(null)
    
    fun getFile(name: String): File = File(dir, "$name.cg")
    
    fun valid(name: String): Boolean = 
        with(getFile(name)){
            exists() && isFile
        }

    fun createPack(name: String): Boolean {
        val f = getFile(name)
        return f.createNewFile().also { if (it) f.appendText("$UID\n") }
    }

    fun deletePack(name: String): Boolean =
        getFile(name).delete()
}