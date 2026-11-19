package com.example.propcare.domain.model

enum class UserRole(val displayName: String) {
    TENANT("Tenant"),
    LANDLORD("Landlord / Manager"),
    TECHNICIAN("Maintenance Staff")
}

enum class ComplaintPriority(val label: String) {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High"),
    EMERGENCY("Emergency")
}

enum class ComplaintStatus(val label: String) {
    SUBMITTED("Submitted"),
    ASSIGNED("Assigned"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed")
}