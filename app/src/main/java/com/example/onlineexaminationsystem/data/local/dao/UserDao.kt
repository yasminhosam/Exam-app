package com.example.onlineexaminationsystem.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.onlineexaminationsystem.data.model.User

@Dao
interface UserDao {
    //login
    @Query("SELECT * FROM users WHERE email= :email AND password= :password")
    suspend fun getUserByEmailAndPassword(email:String,password:String): User?

    // check if email exist
    @Query("SELECT * FROM users WHERE email= :email")
    suspend fun getUserByEmail(email: String):User?

    @Insert
    suspend fun insertUser(user:User):Long
}