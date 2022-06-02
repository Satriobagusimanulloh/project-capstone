package com.bangkit.capstone.c22_ps321.responses

import com.google.gson.annotations.SerializedName

data class LoginResponse(

    @field:SerializedName("loginResult")
    val loginResult: LoginResult,

    @field:SerializedName("error")
    val error: Boolean,

    @field:SerializedName("message")
    val message: String
)

data class LoginResult(

    @field:SerializedName("email")
    val email: String,

    @field:SerializedName("localId")
    val localId: String,

    @field:SerializedName("idToken")
    val idToken: String
)