package com.bangkit.capstone.c22_ps321.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "classify")
class ClassifyEntity(

    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "disease")
    val disease: String,

    @ColumnInfo(name = "description")
    val description: String?,

    @ColumnInfo(name = "treatment")
    val treatment: String?,
)
