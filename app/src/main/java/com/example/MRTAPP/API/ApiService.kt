package com.example.MRTAPP.API

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Url

interface ApiService {
    @POST
    @Headers("Content-Type: text/xml; charset=utf-8")
    fun getRecommendedRoute(@Url url: String, @Body body: RequestBody): Call<String>
}
