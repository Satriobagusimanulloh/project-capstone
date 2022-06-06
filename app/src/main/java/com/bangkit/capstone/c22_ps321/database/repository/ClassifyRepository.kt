package com.bangkit.capstone.c22_ps321.database.repository

import androidx.lifecycle.LiveData
import com.bangkit.capstone.c22_ps321.database.entity.ClassifyEntity
import com.bangkit.capstone.c22_ps321.database.room.ClassifyDao
import com.bangkit.capstone.c22_ps321.helper.AppExecutors

class ClassifyRepository private constructor(
    private val classifyDao: ClassifyDao,
    private val appExecutors: AppExecutors
) {

    val allData: LiveData<List<ClassifyEntity>> = classifyDao.getAllData()

    fun insertData(classifyEntity: List<ClassifyEntity>) {
        appExecutors.diskIO.execute {
            classifyDao.insertData(classifyEntity) }
    }

    fun getDataByDisease(diseaseIndex: Int): LiveData<ClassifyEntity> {
        return classifyDao.getDataByDisease(diseaseIndex)
    }

    companion object {
        @Volatile
        private var INSTANCE: ClassifyRepository? = null
        fun getInstance(
            classifyDao: ClassifyDao,
            appExecutors: AppExecutors
        ): ClassifyRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: ClassifyRepository(classifyDao, appExecutors)
            }.also { INSTANCE = it }
    }
}