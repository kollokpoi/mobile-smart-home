package com.example.myapplication.viewmodels

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.models.LoginRequest
import com.example.myapplication.models.LoginResponse
import com.example.myapplication.services.RetrofitFactory
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.myapplication.classes.Result
import com.example.myapplication.models.User

class LoginViewModel (application: Application) : AndroidViewModel(application) {
    private val _loginResult = MutableLiveData<Result<LoginResponse>>()
    val loginResult: LiveData<Result<LoginResponse>> = _loginResult

    private val sharedPrefs = application.getSharedPreferences("auth", Context.MODE_PRIVATE)
    private val authApi = RetrofitFactory.instance

    init {
        checkAuthToken()
    }

    private fun checkAuthToken() {
        val token = sharedPrefs.getString("token", null)
        if (token != null) {
            fetchUserData(token)
        }
    }

    private fun fetchUserData(token: String) {
        _loginResult.postValue(Result.Loading)

        val call = authApi.getUserProfile("Bearer $token")
        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    response.body()?.let { user ->
                        _loginResult.postValue(Result.Success(LoginResponse(token,user)))
                    } ?: run {
                        clearAuthData()
                        _loginResult.postValue(
                            Result.Error(
                                Exception("Empty user data"),
                                "User data not found"
                            )
                        )
                    }
                } else {
                    clearAuthData()
                    _loginResult.postValue(
                        Result.Error(
                            Exception("HTTP ${response.code()}"),
                            "Session expired"
                        )
                    )
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                _loginResult.postValue(
                    Result.Error(
                        Exception(t),
                        "Network error"
                    )
                )
            }
        })
    }
    private fun clearAuthData() {
        sharedPrefs.edit().remove("token").apply()
    }
    fun login(login: String, password: String) {
        val loginData = LoginRequest(login,password)
        val call = RetrofitFactory.instance.login(loginData)
        _loginResult.postValue(Result.Loading)
        viewModelScope.launch {
            call.enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.let { loginResponse ->
                            _loginResult.postValue(Result.Success(loginResponse))
                        } ?: run {
                            _loginResult.postValue(
                                Result.Error(
                                    Exception("Empty response"),
                                    "Server returned no data"
                                )
                            )
                        }
                    } else {
                        _loginResult.postValue(
                            Result.Error(
                                Exception("HTTP ${response.code()}")
                            )
                        )
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    _loginResult.postValue(
                        Result.Error(
                            Exception(t),
                            t.message ?: "Network request failed"
                        )
                    )
                }
            })
        }
    }
}