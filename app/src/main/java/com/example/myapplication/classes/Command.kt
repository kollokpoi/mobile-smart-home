package com.example.myapplication.classes

import com.example.myapplication.services.RetrofitFactory
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

data class Command (
    val commandName:String,
    val commandToSend:String,
    val itemId:Int,
    val shouldReturn:Boolean = false,
    var image : String? = null,
    var id: Int? = null,
    var jsonBody:String?=""
)