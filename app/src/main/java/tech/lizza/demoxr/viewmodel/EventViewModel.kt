package tech.lizza.demoxr.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tech.lizza.demoxr.data.Talk
import tech.lizza.demoxr.data.Speaker
import tech.lizza.demoxr.repository.EventRepository

class EventViewModel(application: Application) : AndroidViewModel(application) {
    
    companion object {
        private const val TAG = "EventViewModel"
    }
    
    private val repository = EventRepository(application)
    
    private val _speakers = MutableStateFlow<List<Speaker>>(emptyList())
    val speakers: StateFlow<List<Speaker>> = _speakers.asStateFlow()
    
    private val _talks = MutableStateFlow<List<Talk>>(emptyList())
    val talks: StateFlow<List<Talk>> = _talks.asStateFlow()
    
    private val _selectedDay = MutableStateFlow(1)
    val selectedDay: StateFlow<Int> = _selectedDay.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _selectedTalk = MutableStateFlow<Talk?>(null)
    val selectedTalk: StateFlow<Talk?> = _selectedTalk.asStateFlow()

    // Theme state
    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()
    
    // Dialog states
    private val _showLocationDialog = MutableStateFlow(false)
    val showLocationDialog: StateFlow<Boolean> = _showLocationDialog.asStateFlow()
    
    private val _showVirtualInfoDialog = MutableStateFlow(false)
    val showVirtualInfoDialog: StateFlow<Boolean> = _showVirtualInfoDialog.asStateFlow()
    
    init {
        loadData()
    }
    
    private fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val speakersResult = repository.getSpeakers()
                val talksResult = repository.getTalks()
                
                _speakers.value = speakersResult
                _talks.value = talksResult
            } catch (e: Exception) {
                _error.value = "Error loading data: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun selectDay(day: Int) {
        _selectedDay.value = day
    }
    
    fun getTalksByDay(day: Int): List<Talk> {
        return _talks.value.filter { it.day == day }
    }
    
    fun getSpeakerById(id: String): Speaker? {
        return _speakers.value.find { it.id == id }
    }
    
    
    fun clearError() {
        _error.value = null
    }
    
    fun selectTalk(talk: Talk?) {
        Log.d(TAG, "Selecting talk: ${talk?.title ?: "null"}")
        _selectedTalk.value = talk
    }

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }
    
    fun showLocationDialog() {
        _showLocationDialog.value = true
    }
    
    fun hideLocationDialog() {
        _showLocationDialog.value = false
    }
    
    fun showVirtualInfoDialog() {
        _showVirtualInfoDialog.value = true
    }
    
    fun hideVirtualInfoDialog() {
        _showVirtualInfoDialog.value = false
    }
}