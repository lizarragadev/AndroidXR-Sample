package tech.lizza.demoxr.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tech.lizza.demoxr.data.Talk
import tech.lizza.demoxr.data.Speaker
import java.io.IOException

class EventRepository(private val context: Context) {
    private val gson = Gson()
    
    suspend fun getSpeakers(): List<Speaker> = withContext(Dispatchers.IO) {
        try {
            val jsonString = context.assets.open("speakers.json").bufferedReader().use { it.readText() }
            val listType = object : TypeToken<List<Speaker>>() {}.type
            gson.fromJson(jsonString, listType)
        } catch (e: IOException) {
            emptyList()
        }
    }
    
    suspend fun getTalks(): List<Talk> = withContext(Dispatchers.IO) {
        try {
            val jsonString = context.assets.open("talks.json").bufferedReader().use { it.readText() }
            val listType = object : TypeToken<List<Talk>>() {}.type
            gson.fromJson(jsonString, listType)
        } catch (e: IOException) {
            emptyList()
        }
    }
}
