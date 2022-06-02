package com.bangkit.capstone.c22_ps321.user

data class User(
    val email: String,
    val password: String,
    val isLogin: Boolean,
    val token: String
)