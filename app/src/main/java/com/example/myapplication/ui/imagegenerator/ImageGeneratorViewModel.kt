package com.example.myapplication.ui.imagegenerator

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.classes.Command
import com.example.myapplication.classes.Styles
import com.example.myapplication.models.ImageRequestModel
import com.example.myapplication.services.RetrofitFactory
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ImageGeneratorViewModel : ViewModel() {
    init {
        getStyles()
    }
    private var pickedStyle : MutableLiveData<Styles> = MutableLiveData()
    var imageBase64 : MutableLiveData<String> = MutableLiveData()
    var styles : MutableLiveData<List<Styles>> = MutableLiveData(emptyList())

    var failure : MutableLiveData<Boolean> = MutableLiveData(false)
    var loading : MutableLiveData<Boolean> = MutableLiveData(false)
    var changesSaved : MutableLiveData<Boolean> = MutableLiveData(false)

    fun setStyle(position:Int){
        pickedStyle.value = styles.value?.get(position)
    }
    fun getImage(prompt:String){
        loading.value = true
        val model = ImageRequestModel(prompt,pickedStyle.value!!.name)
        val call = RetrofitFactory.instanceWithoutTimeOut.generateImage(model)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    response.body()?.string()?.let { responseBody ->
                        imageBase64.value = responseBody
                    }
                    changesSaved.value = true
                } else {
                    failure.value = true
                }
                loading.value = false
            }
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                failure.value = true
                loading.value = false
            }
        })
    }

    private fun getStyles(){
        viewModelScope.launch {
            val call = RetrofitFactory.instance.getStyles()
            call.enqueue(object : Callback<List<Styles>> {
                override fun onResponse(call: Call<List<Styles>>, response: Response<List<Styles>>) {
                    if (response.isSuccessful) {
                        styles.value = response.body()
                        setStyle(0)
                    } else {
                        failure.value = true
                    }
                    loading.value = false
                }
                override fun onFailure(call: Call<List<Styles>>, t: Throwable) {
                    failure.value = true
                    loading.value = false
                }
            })
        }
    }
}