package com.bangkit.capstone.c22_ps321.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bangkit.capstone.c22_ps321.user.User
import com.bangkit.capstone.c22_ps321.user.UserPreferences
import kotlinx.coroutines.launch

class RegisterViewModel(private val preferences: UserPreferences) : ViewModel() {

    fun saveUser(user: User) {
        viewModelScope.launch {
            preferences.saveUser(user)
        }
    }
}