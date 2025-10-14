package tech.lizza.demoxr.repository

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tech.lizza.demoxr.data.Talk
import tech.lizza.demoxr.data.Speaker
import java.io.IOException

class EventRepository(private val context: Context) {
    companion object {
        private const val TAG = "EventRepository"
    }
    
    private val gson = Gson()
    
    suspend fun getSpeakers(): List<Speaker> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Loading speakers...")
            val jsonString = context.assets.open("speakers.json").bufferedReader().use { it.readText() }
            val listType = object : TypeToken<List<Speaker>>() {}.type
            val speakers: List<Speaker> = gson.fromJson(jsonString, listType)
            Log.d(TAG, "Speakers loaded: ${speakers.size}")
            speakers
        } catch (e: IOException) {
            Log.e(TAG, "Error loading speakers", e)
            emptyList()
        }
    }
    
    suspend fun getTalks(): List<Talk> = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Loading talks...")
            val jsonString = context.assets.open("talks.json").bufferedReader().use { it.readText() }
            val listType = object : TypeToken<List<Talk>>() {}.type
            val talks: List<Talk> = gson.fromJson(jsonString, listType)
            Log.d(TAG, "Talks loaded: ${talks.size}")
            talks
        } catch (e: IOException) {
            Log.e(TAG, "Error loading talks", e)
            emptyList()
        }
    }
    
    suspend fun getTalksByDay(day: Int): List<Talk> {
        val talks = getTalks()
        return talks.filter { it.day == day }
    }
    
    suspend fun getSpeakerById(id: String): Speaker? {
        val speakers = getSpeakers()
        return speakers.find { it.id == id }
    }
    
}
