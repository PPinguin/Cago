package com.cago.core.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cago.core.models.server.PackInfo

@Entity
data class Pack(
    @ColumnInfo(name="name") var name: String,
    @ColumnInfo(name="key")  var key: String?
){
    @PrimaryKey(autoGenerate = true) var uid: Int = 0
    @ColumnInfo(name="actual") var actual: Boolean = false 
        set(value) { field = (key != null) && value }
    fun getInfo() = if(key != null) PackInfo(name, key) else null
}
