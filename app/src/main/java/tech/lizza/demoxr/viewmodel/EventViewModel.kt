package tech.lizza.demoxr.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tech.lizza.demoxr.data.Talk
import tech.lizza.demoxr.data.Speaker
import tech.lizza.demoxr.data.Sponsor
import tech.lizza.demoxr.repository.EventRepository
import tech.lizza.demoxr.ui.components.TabType

class EventViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = EventRepository(application)
    
    private val _speakers = MutableStateFlow<List<Speaker>>(emptyList())
    val speakers: StateFlow<List<Speaker>> = _speakers.asStateFlow()
    
    private val _talks = MutableStateFlow<List<Talk>>(emptyList())
    val talks: StateFlow<List<Talk>> = _talks.asStateFlow()
    
    private val _sponsors = MutableStateFlow<List<Sponsor>>(emptyList())
    val sponsors: StateFlow<List<Sponsor>> = _sponsors.asStateFlow()
    
    private val _selectedTab = MutableStateFlow(TabType.EXPOSITORES)
    val selectedTab: StateFlow<TabType> = _selectedTab.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    private val _selectedTalk = MutableStateFlow<Talk?>(null)
    val selectedTalk: StateFlow<Talk?> = _selectedTalk.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(false)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()
    
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
                _speakers.value = repository.getSpeakers()
                _talks.value = repository.getTalks()
                _sponsors.value = repository.getSponsors()
            } catch (e: Exception) {
                _error.value = "Error loading data: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun selectTab(tab: TabType) {
        _selectedTab.value = tab
    }
    
    fun getTalksBySpeaker(speakerId: String): Talk? = _talks.value.find { it.speakerId == speakerId }
    
    fun getSpeakerById(id: String): Speaker? = _speakers.value.find { it.id == id }
    
    fun clearError() {
        _error.value = null
    }
    
    fun selectTalk(talk: Talk?) {
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
