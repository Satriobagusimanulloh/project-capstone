package com.bangkit.capstone.c22_ps321.responses

import com.google.gson.annotations.SerializedName

data class RegisterResponse(

    @field:SerializedName("error")
    val code: String,

    @field:SerializedName("message")
    val message: String
)