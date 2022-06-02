package com.bangkit.capstone.c22_ps321.retrofit

import com.bangkit.capstone.c22_ps321.responses.LoginResponse
import com.bangkit.capstone.c22_ps321.responses.RegisterResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {

    // register
    @FormUrlEncoded
    @POST("accounts:signUp?key=")
    fun register(
        @Field("email") email: String,
        @Field("password") password:String
    ): Call<RegisterResponse>

    // login
    @FormUrlEncoded
    @POST("accounts:signInWithPassword?key=")
    fun login(
        @Field("email") email:String,
        @Field("password") password: String
    ):Call<LoginResponse>

    // get plants data
}