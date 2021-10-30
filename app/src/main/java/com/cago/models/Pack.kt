package com.cago.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cago.models.server.PackInfo

@Entity
data class Pack(
    @ColumnInfo(name="name") var name: String,
    @ColumnInfo(name="key")  var key: String?
){
    @PrimaryKey(autoGenerate = true) var uid: Int = 0
    fun getInfo() = if(key != null) PackInfo(name, key) else null
}
