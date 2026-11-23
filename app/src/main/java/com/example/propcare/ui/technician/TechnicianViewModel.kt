
package com.example.propcare.ui.technician

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.propcare.data.repository.PropCareRepository
import com.example.propcare.data.local.ComplaintEntity
import com.example.propcare.data.remote.RetrofitClient
import com.example.propcare.data.remote.CurrentWeather
import com.example.propcare.domain.model.ComplaintStatus
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@Suppress("unused")
class TechnicianViewModel(
    private val repository: PropCareRepository,
    technicianId: Int
) : ViewModel() {

    private val api = RetrofitClient.weatherApi

    val tasks: StateFlow<List<ComplaintEntity>> =
        repository.getComplaintsByTechnician(technicianId)
            .map { complaints ->
                complaints.filter {
                    it.status == ComplaintStatus.ASSIGNED ||
                            it.status == ComplaintStatus.IN_PROGRESS
                }.sortedByDescending { it.priority.ordinal }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptyList()
            )

    private val _weather = MutableStateFlow<CurrentWeather?>(null)
    val weather = _weather.asStateFlow()

    private val _weatherError = MutableStateFlow<String?>(null)
    val weatherError = _weatherError.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    init {
        loadWeather()
    }

    fun loadWeather() {
        viewModelScope.launch {
            _loading.value = true
            _weatherError.value = null

            try {
                _weather.value = api.getCurrentWeather().currentWeather
            } catch (_: Exception) {
                _weatherError.value = "Weather unavailable. Check internet connection."
            } finally {
                _loading.value = false
            }
        }
    }

    fun markInProgress(complaint: ComplaintEntity) {
        viewModelScope.launch {
            repository.updateComplaint(
                complaint.copy(
                    status = ComplaintStatus.IN_PROGRESS,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }

    fun markCompleted(
        complaint: ComplaintEntity,
        notes: String,
        photoUri: String?
    ) {
        viewModelScope.launch {
            repository.updateComplaint(
                complaint.copy(
                    status = ComplaintStatus.RESOLVED,
                    technicianNotes = notes,
                    completionPhotoUri = photoUri,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }
}
