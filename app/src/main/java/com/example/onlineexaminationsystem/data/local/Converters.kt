package com.example.onlineexaminationsystem.data.local

import androidx.room.TypeConverter
import com.example.onlineexaminationsystem.data.model.Role
import com.example.onlineexaminationsystem.data.model.Status
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Date
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class Converters{
    @TypeConverter
    fun fromList(list: List<String>):String{
        return Gson().toJson(list)
    }
    //form database
    @TypeConverter
    fun fromStringList(value:String):List<String>{
        val listType=object:TypeToken<List<String>>() {}.type
        return Gson().fromJson(value,listType)

    }
    @TypeConverter
    fun dateTOTimestamp(date: Date?):Long?{
        return date?.time
    }

    @TypeConverter
    fun fromTimestamp(value: Long?):Date?{
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun fromDuration(value: Long):Duration{
        return value.milliseconds
    }

    @TypeConverter
    fun durationTOLong(duration: Duration):Long{
        return duration.inWholeMilliseconds
    }
    @TypeConverter
    fun fromRole(role:Role):String{
        return role.name
    }
    @TypeConverter
    fun toRole(value: String):Role{
        return Role.valueOf(value)
    }
    @TypeConverter
    fun fromStatus(status: Status):String{
        return status.name
    }
    @TypeConverter
    fun toStatus(value: String):Status{
       return Status.valueOf(value)
    }
}