package com.example.onlineexaminationsystem.domain.repository

import com.example.onlineexaminationsystem.domain.model.BasicSession
import com.example.onlineexaminationsystem.domain.model.Role
import com.example.onlineexaminationsystem.domain.model.User
import com.example.onlineexaminationsystem.domain.util.Resource


interface AuthRepository {

    suspend fun signUp(name: String, email: String, password: String, role: Role): Result<User>
    suspend fun login(email: String, password: String): Result<User>
   suspend fun logout()
     fun getCurrentSession(): BasicSession?
    suspend fun isEmailVerified():Boolean
    suspend fun resetPassword(email: String): Resource<Unit>
}