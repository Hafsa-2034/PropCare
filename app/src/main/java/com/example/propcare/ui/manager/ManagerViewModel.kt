package com.example.propcare.ui.manager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.propcare.data.local.ComplaintEntity
import com.example.propcare.data.local.UserEntity
import com.example.propcare.data.repository.PropCareRepository
import com.example.propcare.domain.model.ComplaintStatus
import com.example.propcare.domain.model.PriorityLevel
import com.example.propcare.domain.model.UserRole
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ManagerViewModel(
    private val repository: PropCareRepository,
    private val complaintId: Int
) : ViewModel() {

    val complaint: StateFlow<ComplaintEntity?> = repository.getComplaintById(complaintId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val technicians: StateFlow<List<UserEntity>> = repository.getAllUsers()
        .map { users -> users.filter { it.role == UserRole.TECHNICIAN } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // One-off messages for the Snackbar
    private val _messages = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val messages: SharedFlow<String> = _messages.asSharedFlow()

    fun updatePriority(priority: PriorityLevel) = update("Priority set to ${priority.name}") {
        it.copy(priority = priority)
    }

    fun assignTechnician(technician: UserEntity) = update("Assigned to ${technician.name}") {
        it.copy(
            assignedTechnicianId = technician.id,
            // Only move OPEN -> ASSIGNED; don't regress IN_PROGRESS/RESOLVED
            status = if (it.status == ComplaintStatus.OPEN) ComplaintStatus.ASSIGNED else it.status
        )
    }

    fun updateStatus(status: ComplaintStatus) {
        viewModelScope.launch {
            val current = complaint.value ?: return@launch
            if (status == ComplaintStatus.ASSIGNED && current.assignedTechnicianId == null) {
                _messages.emit("Assign a technician first")
                return@launch
            }
            repository.updateComplaint(
                current.copy(status = status, updatedAt = System.currentTimeMillis())
            )
            _messages.emit("Status updated to ${status.name}")
        }
    }

    private fun update(message: String, change: (ComplaintEntity) -> ComplaintEntity) {
        viewModelScope.launch {
            val current = complaint.value ?: return@launch
            repository.updateComplaint(change(current).copy(updatedAt = System.currentTimeMillis()))
            _messages.emit(message)
        }
    }

    companion object {
        fun factory(repository: PropCareRepository, complaintId: Int) =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    ManagerViewModel(repository, complaintId) as T
            }
    }
}