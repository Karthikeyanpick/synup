package com.example.synup.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Variations(
    val name : String,
    val price : Int,
    val default : Int,
    val id : String,
    val inStock : Int,
    val isVeg : Int,
    @SerializedName("isExclude")
    var isExclude : Boolean

):Serializable

