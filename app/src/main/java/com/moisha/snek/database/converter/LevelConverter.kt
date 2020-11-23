package com.moisha.snek.database.converter

import android.arch.persistence.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.ArrayList

class LevelConverter {

    private val gson = Gson()

    @TypeConverter
    fun listToJson(value: List<IntArray>?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun jsonToList(value: String?): List<IntArray>? {
        val result: MutableList<IntArray> = mutableListOf()

        val intArrays: List<ArrayList<Int>> = gson.fromJson(value, object : TypeToken<List<ArrayList<Int>>>() {}.type)

        for (i in intArrays) {
            val pair: IntArray = IntArray(i.size)
            for (j in 0..i.size - 1) {
                pair[j] = i[j]
            }
            result.add(pair)
        }

        return result.toList()
    }

    @TypeConverter
    fun intArrayToJson(value: IntArray?): String? {
        return gson.toJson(value)
    }

    @TypeConverter
    fun jsonToIntArray(value: String?): IntArray? {
        return gson.fromJson(value, object : TypeToken<IntArray>() {}.type)
    }
}