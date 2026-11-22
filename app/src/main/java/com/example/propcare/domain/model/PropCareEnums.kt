
package com.example.propcare.domain.model

enum class UserRole(val displayName: String) {
    TENANT("Tenant"),
    LANDLORD("Landlord / Manager"),
    TECHNICIAN("Maintenance Staff")
}

enum class IssueCategory(val label: String) {
    PLUMBING("Plumbing"),
    ELECTRICAL("Electrical"),
    STRUCTURAL("Structural"),
    APPLIANCE("Appliance"),
    HVAC("HVAC"),
    GENERAL("General")
}

enum class PriorityLevel(val label: String) {
    LOW("Low"),
    MEDIUM("Medium"),
    HIGH("High"),
    URGENT("Urgent")
}

enum class ComplaintStatus(val label: String) {
    OPEN("Open"),
    ASSIGNED("Assigned"),
    IN_PROGRESS("In Progress"),
    RESOLVED("Resolved")
}
