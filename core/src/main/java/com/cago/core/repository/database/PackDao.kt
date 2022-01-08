package com.cago.core.repository.database

import androidx.room.*
import com.cago.core.models.Pack
import kotlinx.coroutines.flow.Flow

@Dao
interface PackDao {
    @Query("SELECT * FROM pack")
    fun getAllFlow(): Flow<List<Pack>>
    @Query("SELECT * FROM pack")
    fun getList(): List<Pack>
    @Query("SELECT * FROM pack WHERE name LIKE :name")
    fun getByName(name: String): Pack
    @Insert
    fun insert(pack: Pack)
    @Delete
    fun delete(pack: Pack)
    @Update
    fun update(pack: Pack)
}