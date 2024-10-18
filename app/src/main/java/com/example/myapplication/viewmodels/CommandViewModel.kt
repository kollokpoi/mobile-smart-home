package com.example.myapplication.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.classes.Command
import com.example.myapplication.classes.Item
import com.example.myapplication.classes.Verb
import com.example.myapplication.services.RetrofitFactory
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CommandViewModel: ViewModel() {
    private val _currentCommand = MutableLiveData<Command>()
    private val _verbs = MutableLiveData<List<Verb>>(emptyList())

    val currentCommand: LiveData<Command> get() = _currentCommand
    val verbs: LiveData<List<Verb>> get() = _verbs

    private var commandId = 0
    var failure : MutableLiveData<Boolean> = MutableLiveData(false)
    var loading : MutableLiveData<Boolean> = MutableLiveData(false)
    var changesSaved : MutableLiveData<Boolean> = MutableLiveData(false)
    var deleted : MutableLiveData<Boolean> = MutableLiveData(false)

    fun setId(id:Int){
        commandId = id
        setCommand()
        setVerbs()
    }

    private fun setCommand(){
        viewModelScope.launch {
            val call = RetrofitFactory.instance.getCommand(commandId);
            call.enqueue(object : Callback<Command> {
                override fun onResponse(call: Call<Command>, response: Response<Command>) {
                    if (response.isSuccessful) {
                        _currentCommand.value = response.body()
                    }
                    else{
                        failure.value = true
                    }
                }
                override fun onFailure(call: Call<Command>, t: Throwable) {
                    failure.value = true
                }
            })
        }
    }

    private fun setVerbs(){
        viewModelScope.launch {
            val call = RetrofitFactory.instance.commandVerbs(commandId);
            call.enqueue(object : Callback<List<Verb>> {
                override fun onResponse(call: Call<List<Verb>>, response: Response<List<Verb>>) {
                    if (response.isSuccessful) {
                        _verbs.value = response.body()
                    }
                    else{
                        failure.value = true
                    }
                }
                override fun onFailure(call: Call<List<Verb>>, t: Throwable) {
                    failure.value = true
                }
            })
        }
    }

    fun createVerb(verb: Verb){
        viewModelScope.launch {
            loading.value = true
            changesSaved.value = false
            val call = RetrofitFactory.instance.createVerb(verb)
            call.enqueue(object : Callback<Int> {
                override fun onResponse(call: Call<Int>, response: Response<Int>) {
                    if (response.isSuccessful) {
                        verb.id = response.body()
                        _verbs.value = _verbs.value?.plus(verb)
                        changesSaved.value = true
                    }
                    else{
                        failure.value = true
                    }
                    loading.value = false
                }
                override fun onFailure(call: Call<Int>, t: Throwable) {
                    loading.value = false
                    failure.value = true
                }
            })
        }
    }

    fun editCommand(command: Command, image: MultipartBody.Part?){
        viewModelScope.launch {
            loading.value = true
            changesSaved.value = false

            val commandName = command.commandName.toRequestBody("text/plain".toMediaTypeOrNull())
            val commandToSend = command.commandToSend.toRequestBody("text/plain".toMediaTypeOrNull())
            val shouldReturn = command.shouldReturn.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val jsonBody = command.jsonBody?.toRequestBody("text/plain".toMediaTypeOrNull())
            val call = RetrofitFactory.instance.editCommand(
                commandId,
                commandName,
                commandToSend,
                shouldReturn,
                jsonBody,
                image)

            call.enqueue(object : Callback<Command> {
                override fun onResponse(call: Call<Command>, response: Response<Command>) {
                    if (response.isSuccessful) {
                        _currentCommand.value = response.body()
                        changesSaved.value = true
                    } else {
                        failure.value = true
                    }
                    loading.value = false
                }
                override fun onFailure(call: Call<Command>, t: Throwable) {
                    failure.value = true
                    loading.value = false
                }
            })
        }
    }

    fun deleteCommand(){
        viewModelScope.launch {
            loading.value = true
            val call = RetrofitFactory.instance.deleteCommand(commandId)
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

    fun deleteVerb(verb: Verb){
        viewModelScope.launch {
            loading.value = true
            val call = RetrofitFactory.instance.deleteVerb(verb.id!!)
            call.enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        val currentList = _verbs.value?.toMutableList() ?: mutableListOf()
                        currentList.remove(verb)
                        _verbs.value = currentList
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
}