package com.example.synup.data.source.remote

import com.example.synup.data.source.remote.handler.response.VariantResponse
import retrofit2.Call
import retrofit2.http.GET


interface VariantApi {
    @GET("/bins/19u0sf")
    fun getList(): Call<VariantResponse>
}