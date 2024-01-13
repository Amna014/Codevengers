package com.example.jobvengers.network

import com.example.jobvengers.data.ApiRequest
import com.example.jobvengers.data.ApiResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface IRequestContact {
    @POST("Server.php")
    fun makeApiCall(@Body request: ApiRequest): Call<ApiResponse>
}