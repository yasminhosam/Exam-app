package com.example.onlineexaminationsystem.data.repository

import com.example.onlineexaminationsystem.data.local.AppDatabase

import com.example.onlineexaminationsystem.data.model.User
import com.example.onlineexaminationsystem.data.local.dao.UserDao
import com.example.onlineexaminationsystem.data.model.Role
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor (private val userDao:UserDao){
    private val _currentUser= MutableStateFlow<User?>(null)
    val currentUser =_currentUser.asStateFlow()

   suspend fun registerStudent(name: String, email: String, password: String): User {
       var newUser=User(
           id = 0,
           name=name,
           email = email,
           password = password,
           role = Role.STUDENT
       )
      val userId= userDao.insertUser(newUser)
       _currentUser.value=newUser.copy(id = userId)

        return _currentUser.value!!

    }

    suspend fun login(email: String, password: String): User? {
        val user = userDao.getUserByEmailAndPassword(email,password)
        if(user!=null)
            _currentUser.value=user
        return user
    }

    fun logout(){
        _currentUser.value=null
    }
}