package com.bangkit.capstone.c22_ps321.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.bangkit.capstone.c22_ps321.user.User
import com.bangkit.capstone.c22_ps321.user.UserPreferences

class SplashScreenViewModel(private val preferences: UserPreferences) : ViewModel()  {
    fun getUser(): LiveData<User> {
        return preferences.getUser().asLiveData()
    }
}