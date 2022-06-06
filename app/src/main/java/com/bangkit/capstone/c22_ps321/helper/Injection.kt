package com.bangkit.capstone.c22_ps321.helper

import android.content.Context
import com.bangkit.capstone.c22_ps321.database.repository.ClassifyRepository
import com.bangkit.capstone.c22_ps321.database.room.ClassifyDatabase

object Injection {
    fun provideRepository(context: Context): ClassifyRepository {
        val database = ClassifyDatabase.getInstance(context)
        val dao = database.classifyDao()
        val appExecutors = AppExecutors()
        return ClassifyRepository.getInstance(dao, appExecutors)
    }
}