package com.example.synup.data.source.remote

import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit


object RetrofitService {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.myjson.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    fun <S> cteateService(serviceClass: Class<S>): S {
        return retrofit.create(serviceClass)
    }

}