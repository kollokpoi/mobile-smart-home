package com.example.myapplication.models

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id") val id: Int,
)