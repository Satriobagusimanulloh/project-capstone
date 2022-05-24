package com.bangkit.capstone.c22_ps321.retrofit

import com.bangkit.capstone.c22_ps321.responses.RegisterResponse
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {

    // register
    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password:String
    ): Call<RegisterResponse>

    // login
    
    // get plants data
}