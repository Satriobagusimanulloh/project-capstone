package com.bangkit.capstone.c22_ps321.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bangkit.capstone.c22_ps321.database.repository.ClassifyRepository
import com.bangkit.capstone.c22_ps321.helper.Injection

class ScanningViewModelFactory (private val classifyRepository: ClassifyRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(ScanningViewModel::class.java) -> {
                ScanningViewModel(classifyRepository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)

        }
    }

    companion object {
        @Volatile
        private var instance: ScanningViewModelFactory? = null
        fun getInstance(context: Context): ScanningViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: ScanningViewModelFactory(Injection.provideRepository(context))
            }.also { instance = it }
    }
}