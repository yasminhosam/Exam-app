package com.example.onlineexaminationsystem.data.model

import androidx.annotation.DrawableRes
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName ="categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id:Long,

    val name:String,
    @DrawableRes val imageRes: Int
)
