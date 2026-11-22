package com.example.propcare.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.propcare.domain.model.IssueCategory
import com.example.propcare.domain.model.PriorityLevel
import com.example.propcare.domain.model.ComplaintStatus

@Entity(tableName = "complaints")
data class ComplaintEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val description: String,
    val unitNumber: String,
    val category: IssueCategory,
    val priority: PriorityLevel = PriorityLevel.MEDIUM,
    val status: ComplaintStatus = ComplaintStatus.OPEN,
    val photoUri: String? = null,
    val completionPhotoUri: String? = null,
    val technicianNotes: String? = null,
    val assignedTechnicianId: Int? = null,
    val tenantId: Int,
    val createdAt: Long,
    val updatedAt: Long
)
