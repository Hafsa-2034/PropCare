package com.example.propcare.ui.complaints

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.propcare.data.local.ComplaintEntity
import com.example.propcare.data.repository.PropCareRepository
import com.example.propcare.domain.model.ComplaintStatus
import com.example.propcare.domain.model.IssueCategory
import com.example.propcare.domain.model.UserRole
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

// ---------- Submit form ----------
data class ComplaintFormState(
    val title: String = "",
    val description: String = "",
    val unitNumber: String = "",
    val category: IssueCategory = IssueCategory.GENERAL,
    val photoUri: String? = null,
    val titleError: String? = null,
    val descriptionError: String? = null,
    val unitError: String? = null,
    val isSaving: Boolean = false
)

sealed interface ComplaintEvent {
    data object Saved : ComplaintEvent
    data class Failed(val message: String) : ComplaintEvent
}

// ---------- List ----------
sealed interface ComplaintListUiState {
    data object Loading : ComplaintListUiState
    data object Empty : ComplaintListUiState
    data class Success(val complaints: List<ComplaintEntity>) : ComplaintListUiState
    data class Error(val message: String) : ComplaintListUiState
}

class ComplaintViewModel(
    private val repository: PropCareRepository
) : ViewModel() {

    // ===== Submit form =====
    private val _form = MutableStateFlow(ComplaintFormState())
    val form: StateFlow<ComplaintFormState> = _form.asStateFlow()

    private val _events = MutableSharedFlow<ComplaintEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<ComplaintEvent> = _events.asSharedFlow()

    fun onTitleChange(value: String) = _form.update { it.copy(title = value, titleError = null) }
    fun onDescriptionChange(value: String) = _form.update { it.copy(description = value, descriptionError = null) }
    fun onUnitChange(value: String) = _form.update { it.copy(unitNumber = value, unitError = null) }
    fun onCategoryChange(value: IssueCategory) = _form.update { it.copy(category = value) }
    fun onPhotoSelected(uri: String?) = _form.update { it.copy(photoUri = uri) }

    fun submit() {
        val f = _form.value
        if (f.isSaving) return

        // 1. Validate
        val titleError = when {
            f.title.isBlank() -> "Title is required"
            f.title.trim().length < 5 -> "Title must be at least 5 characters"
            else -> null
        }
        val descriptionError = when {
            f.description.isBlank() -> "Description is required"
            f.description.trim().length < 10 -> "Please describe the problem (at least 10 characters)"
            else -> null
        }
        val unitError = when {
            f.unitNumber.isBlank() -> "Unit number is required"
            !f.unitNumber.trim().matches(Regex("[A-Za-z0-9-]+")) -> "Use letters, numbers or - only"
            else -> null
        }
        if (titleError != null || descriptionError != null || unitError != null) {
            _form.update {
                it.copy(titleError = titleError, descriptionError = descriptionError, unitError = unitError)
            }
            return
        }

        // 2. Save through the repository
        viewModelScope.launch {
            _form.update { it.copy(isSaving = true) }
            try {
                // On first launch the sample users are seeded asynchronously, so wait briefly
                val tenant = withTimeoutOrNull(3_000) {
                    repository.getUsersByRole(UserRole.TENANT.name).first { it.isNotEmpty() }
                }?.first()

                if (tenant == null) {
                    _events.emit(ComplaintEvent.Failed("No tenant account found. Please try again."))
                } else {
                    val now = System.currentTimeMillis()
                    repository.insertComplaint(
                        ComplaintEntity(
                            title = f.title.trim(),
                            description = f.description.trim(),
                            unitNumber = f.unitNumber.trim().uppercase(),
                            category = f.category,
                            photoUri = f.photoUri,
                            tenantId = tenant.id,
                            createdAt = now,
                            updatedAt = now
                        )
                    )
                    _events.emit(ComplaintEvent.Saved)
                }
            } catch (e: Exception) {
                _events.emit(ComplaintEvent.Failed("Could not save the complaint. Please try again."))
            } finally {
                _form.update { it.copy(isSaving = false) }
            }
        }
    }

    // ===== List: search + status filter =====
    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _filter = MutableStateFlow<ComplaintStatus?>(null)
    val filter: StateFlow<ComplaintStatus?> = _filter.asStateFlow()

    fun onQueryChange(value: String) { _query.value = value }
    fun setFilter(status: ComplaintStatus?) { _filter.value = status }

    val listState: StateFlow<ComplaintListUiState> =
        combine(repository.getAllComplaints(), _query, _filter) { all, query, status ->
            val q = query.trim()
            val filtered = all.filter { c ->
                (status == null || c.status == status) &&
                        (q.isEmpty() ||
                                c.title.contains(q, ignoreCase = true) ||
                                c.unitNumber.contains(q, ignoreCase = true) ||
                                c.description.contains(q, ignoreCase = true))
            }
            val state: ComplaintListUiState =
                if (filtered.isEmpty()) ComplaintListUiState.Empty
                else ComplaintListUiState.Success(filtered)
            state
        }
            .catch { emit(ComplaintListUiState.Error("Could not load complaints.")) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ComplaintListUiState.Loading)

    companion object {
        fun factory(repository: PropCareRepository) =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T =
                    ComplaintViewModel(repository) as T
            }
    }
}

