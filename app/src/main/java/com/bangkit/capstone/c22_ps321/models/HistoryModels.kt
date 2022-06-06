package com.bangkit.capstone.c22_ps321.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class HistoryModels(
    val name: String?,
    val disease: String?,
    val description: String?,
    val treatment: String?,
    val photoUrl: String?,
): Parcelable
