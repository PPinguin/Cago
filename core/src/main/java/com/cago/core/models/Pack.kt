package com.cago.core.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Pack(
    @ColumnInfo(name="name") var name: String,
    @ColumnInfo(name="path") var path: String? = null
){
    @PrimaryKey(autoGenerate = true) var uid: Int = 0
}
