package com.example.onlineexaminationsystem.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long,

    val name: String,
    val email: String,
    val password: String,
    val role :Role
)
enum class Role{
    STUDENT,
    ADMIN
}
