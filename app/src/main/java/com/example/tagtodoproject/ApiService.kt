package com.example.tagtodoproject.api

import com.example.tagtodoproject.model.User
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("users/gunadermawan")
    fun getUserProfile(): Call<User>
}
