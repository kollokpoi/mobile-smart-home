package com.example.myapplication.classes

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(
        val exception: Exception,
        val message: String = exception.message ?: "Unknown error"
    ) : Result<Nothing>()

    object Loading : Result<Nothing>()

    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
    val isLoading: Boolean get() = this is Loading
}