package com.bangkit.capstone.c22_ps321.database.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bangkit.capstone.c22_ps321.database.entity.ClassifyEntity

@Dao
interface ClassifyDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertData(classifyEntity: List<ClassifyEntity>)

    @Query("SELECT * FROM classify ORDER BY id ASC")
    fun getAllData(): LiveData<List<ClassifyEntity>>

    @Query("SELECT * FROM classify WHERE id = :id")
    fun getDataByDisease(id: Int): LiveData<ClassifyEntity>
}