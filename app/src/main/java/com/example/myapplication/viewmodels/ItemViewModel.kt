package com.example.myapplication.viewmodels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.classes.Command
import com.example.myapplication.classes.Item
import com.example.myapplication.services.RetrofitFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.coroutineContext

class ItemViewModel:ViewModel() {
    private val _currentItem = MutableLiveData<Item>()
    private val _commands = MutableLiveData<List<Command>>(emptyList())
    private var itemId = 0

    val currentItem: LiveData<Item> get() = _currentItem
    val commands: LiveData<List<Command>> get() = _commands

    var failure : MutableLiveData<Boolean> = MutableLiveData(false)
    var loading : MutableLiveData<Boolean> = MutableLiveData(false)
    var changesSaved : MutableLiveData<Boolean> = MutableLiveData(false)
    var deleted : MutableLiveData<Boolean> = MutableLiveData(false)

    fun setItemId(itemId:Int){
        this.itemId = itemId
        setItem()
        getCommands()
    }
    fun createCommand(command: Command,image: MultipartBody.Part?){
        viewModelScope.launch {
            loading.value = true
            changesSaved.value = false
            val commandName = command.commandName.toRequestBody("text/plain".toMediaTypeOrNull())
            val commandToSend =
                command.commandToSend.toRequestBody("text/plain".toMediaTypeOrNull())
            val shouldReturn =
                command.shouldReturn.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val itemId = command.itemId.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val jsonBody = command.jsonBody?.toRequestBody("text/plain".toMediaTypeOrNull())
            val call = RetrofitFactory.instance.createCommand(
                commandName,
                commandToSend,
                shouldReturn,
                itemId,
                jsonBody,
                image
            )

            call.enqueue(object : Callback<Command> {
                override fun onResponse(call: Call<Command>, response: Response<Command>) {
                    if (response.isSuccessful) {
                        _commands.value = _commands.value?.plus(response.body()!!)
                        changesSaved.value = true
                    } else {
                        failure.value = true
                    }
                    loading.value = false
                }

                override fun onFailure(call: Call<Command>, t: Throwable) {
                    loading.value = false
                    failure.value = true
                }
            })
        }
    }
    fun editItem(item: Item, image: MultipartBody.Part?){
        viewModelScope.launch {
            loading.value = true
            changesSaved.value = false

            val nameRequestBody = item.name.toRequestBody("text/plain".toMediaTypeOrNull())
            val ipaddrRequestBody = item.ipaddr.toRequestBody("text/plain".toMediaTypeOrNull())
            val call = RetrofitFactory.instance.editItem(itemId,nameRequestBody, ipaddrRequestBody, image)

            call.enqueue(object : Callback<Item> {
                override fun onResponse(call: Call<Item>, response: Response<Item>) {
                    if (response.isSuccessful) {
                        _currentItem.value = response.body()
                        changesSaved.value = true
                    } else {
                        failure.value = true
                    }
                    loading.value = false
                }
                override fun onFailure(call: Call<Item>, t: Throwable) {
                    failure.value = true
                    loading.value = false
                }
            })
        }
    }
    fun deleteItem(){
        viewModelScope.launch {
            loading.value = true
            val call = RetrofitFactory.instance.deleteItem(itemId)
            call.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        deleted.value = true
                    } else {
                        failure.value = true
                    }
                    loading.value = false
                }
                override fun onFailure(call: Call<Void>, t: Throwable) {
                    failure.value = true
                    loading.value = false
                }
            })
        }
    }
    fun executeCommand(commandToExecute:Command){
        val call = RetrofitFactory.instance.executeCommand(commandToExecute.id!!)
        call.timeout()
        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    changesSaved.value = true
                } else {
                    failure.value = true
                }
                loading.value = false
            }
            override fun onFailure(call: Call<Void>, t: Throwable) {
                failure.value = true
                loading.value = false
            }
        })
    }

    private fun setItem() {
        viewModelScope.launch {
            val call = RetrofitFactory.instance.getItem(itemId)
            call.enqueue(object : Callback<Item> {
                override fun onResponse(call: Call<Item>, response: Response<Item>) {
                    if (response.isSuccessful) {
                        _currentItem.value = response.body()
                    }
                    else{
                        failure.value = true
                    }
                }
                override fun onFailure(call: Call<Item>, t: Throwable) {
                    failure.value = true
                }
            })
        }
    }
    private fun getCommands(){
        viewModelScope.launch {
            val call = RetrofitFactory.instance.itemCommands(itemId)
            call.enqueue(object : Callback<List<Command>> {
                override fun onResponse(call: Call<List<Command>>, response: Response<List<Command>>) {
                    if (response.isSuccessful) {
                        _commands.value = response.body()
                    }
                    else{
                        failure.value = true
                    }
                }
                override fun onFailure(call: Call<List<Command>>, t: Throwable) {
                    failure.value = true
                }
            })
        }
    }
}