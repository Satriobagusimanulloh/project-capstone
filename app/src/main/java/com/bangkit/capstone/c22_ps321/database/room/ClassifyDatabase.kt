package com.bangkit.capstone.c22_ps321.database.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.bangkit.capstone.c22_ps321.database.entity.ClassifyEntity

@Database(entities = [ClassifyEntity::class], version = 1, exportSchema = false)
abstract class ClassifyDatabase : RoomDatabase() {
    abstract fun classifyDao(): ClassifyDao

    companion object {
        @Volatile
        private var INSTANCE: ClassifyDatabase? = null
        fun getInstance(context: Context): ClassifyDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    ClassifyDatabase::class.java, "my_plants"
                )
            .fallbackToDestructiveMigration()
            .createFromAsset("my_plants.db")
                    .build()
            }
    }
}