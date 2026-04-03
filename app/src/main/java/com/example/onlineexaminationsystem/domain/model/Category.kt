package com.example.onlineexaminationsystem.domain.model

import androidx.annotation.DrawableRes
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID.randomUUID

@Entity(tableName ="categories")
data class Category(
    @PrimaryKey
    val id:String=randomUUID().toString(),

    val name:String,
    @DrawableRes val imageRes: Int
)
