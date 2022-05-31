package com.bangkit.capstone.c22_ps321.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bangkit.capstone.c22_ps321.user.UserPreferences

class ViewModelFactory(private val preferences: UserPreferences) : ViewModelProvider.NewInstanceFactory() {     
    
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel(preferences) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(preferences) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)

        }
    }
} 