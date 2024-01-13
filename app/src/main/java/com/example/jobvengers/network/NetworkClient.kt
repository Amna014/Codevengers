package com.example.jobvengers.network

import com.example.jobvengers.utils.Configuration
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NetworkClient {
    companion object{
        private var networkClient: Retrofit? = null

        fun getNetworkClient(): Retrofit {
            val tempInstance = networkClient
            return if(tempInstance!=null){
                tempInstance
            }else{
                val instance = Retrofit.Builder()
                    .baseUrl(Configuration.Base_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                networkClient = instance
                instance
            }
        }
    }
}