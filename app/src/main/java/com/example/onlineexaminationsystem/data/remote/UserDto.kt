package com.example.onlineexaminationsystem.data.remote

import com.example.onlineexaminationsystem.domain.model.Role

data class UserDto(
    val id: String ="", // This will be the Firebase Auth UID
    val name: String ="",
    val email: String ="" ,
    val role: String =""
)
