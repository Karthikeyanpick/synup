package com.example.synup.data.source.remote.handler.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.synup.data.source.remote.RetrofitService
import com.example.synup.data.source.remote.VariantApi
import com.example.synup.data.source.remote.handler.response.VariantResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class VariantRepository {
    private var varientRepository: VariantRepository? = null

    fun getInstance(): VariantRepository {
        if (varientRepository == null) {
            varientRepository = VariantRepository()
        }
        return varientRepository as VariantRepository
    }

    private var variantApi: VariantApi? = null

    init {
        variantApi = RetrofitService.cteateService(VariantApi::class.java)
    }

    fun getList(): MutableLiveData<VariantResponse> {
        val listData = MutableLiveData<VariantResponse>()
        variantApi!!.getList().enqueue(object : Callback<VariantResponse> {
            override fun onResponse(
                call: Call<VariantResponse>,
                response: Response<VariantResponse>
            ) {
                Log.e("Sucess", "is NOt")
                if (response.isSuccessful) {
                    listData.value = response.body()
                }
            }

            override fun onFailure(call: Call<VariantResponse>, t: Throwable) {
                Log.e("Failure", t.message.toString())
                listData.value = null
            }
        })
        return listData
    }
}