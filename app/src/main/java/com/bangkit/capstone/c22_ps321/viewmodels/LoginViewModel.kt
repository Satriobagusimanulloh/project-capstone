package com.bangkit.capstone.c22_ps321.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.bangkit.capstone.c22_ps321.user.User
import com.bangkit.capstone.c22_ps321.user.UserPreferences
import kotlinx.coroutines.launch

class LoginViewModel(private val preferences: UserPreferences) : ViewModel() {

    fun getUser(): LiveData<User> {
        return preferences.getUser().asLiveData()
    }

    fun login() {
        viewModelScope.launch {
            preferences.login()
        }
    }

    fun token(user: User){
        viewModelScope.launch {
            preferences.token(user)
        }
    }
}