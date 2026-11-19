package com.example.propcare.navigation

sealed class Screen(val route: String, val title: String) {
    data object Dashboard : Screen("dashboard", "Dashboard")
    data object SubmitComplaint : Screen("submit_complaint", "Report Issue")
    data object ComplaintList : Screen("complaint_list", "Complaints")
    data object ComplaintDetail : Screen("complaint_detail/{complaintId}", "Issue Details") {
        fun createRoute(complaintId: Int) = "complaint_detail/$complaintId"
    }
    data object TechnicianTasks : Screen("technician_tasks", "My Tasks")
    data object Reports : Screen("reports", "Maintenance Reports")
}