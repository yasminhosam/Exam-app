package com.example.onlineexaminationsystem.domain.util

sealed class Resource<T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error<T>(val message: String, val exception: Exception? = null) : Resource<T>()
}