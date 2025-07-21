package com.example.tagtodoproject.api

import com.google.firebase.firestore.auth.User
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("users/gunadermawan")
    fun getUserProfile(): Call<User>
}
