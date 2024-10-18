package com.example.myapplication.ui.itemsfragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.classes.Item
import com.example.myapplication.services.RetrofitFactory
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ItemsViewModel : ViewModel() {
    private val _items = MutableLiveData<List<Item>>(emptyList())
    val items: LiveData<List<Item>> get() = _items

    var failure : MutableLiveData<Boolean> = MutableLiveData(false)
    var loading : MutableLiveData<Boolean> = MutableLiveData(false)
    var changesSaved : MutableLiveData<Boolean> = MutableLiveData(false)

    fun fetchItems() {
        viewModelScope.launch {
            val call = RetrofitFactory.instance.getItems()
            call.enqueue(object : Callback<List<Item>> {
                override fun onResponse(call: Call<List<Item>>, response: Response<List<Item>>) {
                    if (response.isSuccessful) {
                        _items.value = response.body()
                    }
                }

                override fun onFailure(call: Call<List<Item>>, t: Throwable) {
                    failure.value = true
                }
            })
        }
    }
    fun createItem(item: Item, image: MultipartBody.Part?){
        viewModelScope.launch {
            loading.value = true
            changesSaved.value = false

            val nameRequestBody = item.name.toRequestBody("text/plain".toMediaTypeOrNull())
            val ipaddrRequestBody = item.ipaddr.toRequestBody("text/plain".toMediaTypeOrNull())
            val call = RetrofitFactory.instance.createItem(nameRequestBody, ipaddrRequestBody, image)
            call.enqueue(object : Callback<Item> {
                override fun onResponse(call: Call<Item>, response: Response<Item>) {
                    if (response.isSuccessful) {
                        _items.value = _items.value?.plus(response.body()!!)
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
}