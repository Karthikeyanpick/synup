package com.example.synup.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.synup.data.source.remote.handler.repository.VariantRepository
import com.example.synup.data.source.remote.handler.response.VariantResponse


 class MainViewModel : ViewModel(){
    private var mutableLiveData: MutableLiveData<VariantResponse>? = null
    private var variantRepository: VariantRepository? = null

    fun init() {
        if (mutableLiveData != null) {
            return
        }
        variantRepository = VariantRepository()
        variantRepository!!.getInstance()
        mutableLiveData = variantRepository!!.getList()

    }

    fun getVariantRepository(): LiveData<VariantResponse>? {
        return mutableLiveData
    }

}



