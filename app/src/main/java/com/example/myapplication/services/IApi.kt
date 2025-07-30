package com.example.myapplication.services

import com.example.myapplication.classes.Command
import com.example.myapplication.classes.Item
import com.example.myapplication.classes.Styles
import com.example.myapplication.classes.Verb
import com.example.myapplication.models.ImageRequestModel
import com.example.myapplication.models.LoginRequest
import com.example.myapplication.models.LoginResponse
import com.example.myapplication.models.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface IApi {
    @GET("Items")
    fun getItems(): Call<List<Item>>

    @GET("Items/item/{id}")
    fun getItem(@Path("id") id:Int): Call<Item>

    @Multipart
    @POST("Items/Create")
    fun createItem(
        @Part("name") name: RequestBody,
        @Part("ipaddr") ipaddr: RequestBody,
        @Part image: MultipartBody.Part?
    ): Call<Item>

    @Multipart
    @POST("Items/Edit/{id}")
    fun editItem(
        @Path("id") id: Int,
        @Part("name") name: RequestBody,
        @Part("ipaddr") ipaddr: RequestBody,
        @Part image: MultipartBody.Part?
    ): Call<Item>

    @DELETE("Items/delete/{id}")
    fun deleteItem(
        @Path("id") id: Int,
    ): Call<Void>
    @POST("Items/ping/{id}")
    fun pingItem(
        @Path("id") id: Int,
    ): Call<Boolean>

    //commands
    @GET("commands")
    fun getCommands(): Call<List<Command>>
    @GET("commands/itemCommands/{id}")
    fun itemCommands(@Path("id") id:Int): Call<List<Command>>
    @GET("commands/command/{id}")
    fun getCommand(@Path("id") id:Int): Call<Command>
    @Multipart
    @POST("commands/create")
    fun createCommand(
        @Part("commandName") commandName: RequestBody,
        @Part("commandToSend") commandToSend: RequestBody,
        @Part("shouldReturn") shouldReturn: RequestBody,
        @Part("itemId") itemId: RequestBody,
        @Part("jsonBody") jsonBody: RequestBody?,
        @Part image: MultipartBody.Part?
    ): Call<Command>
    @Multipart
    @POST("commands/Edit/{id}")
    fun editCommand(
        @Path("id") id: Int,
        @Part("commandName") commandName: RequestBody,
        @Part("commandToSend") commandToSend: RequestBody,
        @Part("shouldReturn") shouldReturn: RequestBody,
        @Part("jsonBody") jsonBody: RequestBody?,
        @Part image: MultipartBody.Part?
    ): Call<Command>
    @POST("commands/execute/{id}")
    fun executeCommand(@Path("id") id:Int) : Call<Void>
    @DELETE("commands/delete/{id}")
    fun deleteCommand(
        @Path("id") id: Int,
    ): Call<Void>


    //verbs
    @POST("verb/create")
    fun createVerb(@Body verb: Verb):Call<Int>
    @POST("verb/delete/{id}")
    fun deleteVerb(@Path("id") id:Int):Call<Void>
    @GET("verb/commandVerbs/{id}")
    fun commandVerbs(@Path("id") id:Int): Call<List<Verb>>


    //image generate
    @GET("image/styles")
    fun getStyles() : Call<List<Styles>>
    @POST("image/generate")
    fun generateImage(@Body model : ImageRequestModel) : Call<ResponseBody>


    //user
    @POST("/user/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>
    @POST("/checkAuth")
    fun getUserProfile(@Header("Authorization") token: String): Call<User>
}