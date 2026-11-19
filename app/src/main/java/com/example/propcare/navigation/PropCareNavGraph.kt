package com.example.propcare.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.propcare.domain.model.UserRole
import com.example.propcare.ui.dashboard.DashboardScreen

@Composable
fun PropCareNavGraph(
    navController: NavHostController,
    currentRole: UserRole,
    onRoleChange: (UserRole) -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route,
        modifier = modifier
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                currentRole = currentRole,
                onRoleChange = onRoleChange,
                onNavigateToSubmit = { navController.navigate(Screen.SubmitComplaint.route) },
                onNavigateToComplaints = { navController.navigate(Screen.ComplaintList.route) },
                onNavigateToTechTasks = { navController.navigate(Screen.TechnicianTasks.route) },
                onNavigateToReports = { navController.navigate(Screen.Reports.route) }
            )
        }

        // Member 3 plugs SubmitComplaintScreen & ComplaintListScreen here
        composable(Screen.SubmitComplaint.route) {
            PlaceholderScreen("Submit Complaint (Member 3)")
            // SubmitComplaintScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(Screen.ComplaintList.route) {
            PlaceholderScreen("Complaint List (Member 3)")
            // ComplaintListScreen(onComplaintClick = { id ->
            //     navController.navigate(Screen.ComplaintDetail.createRoute(id))
            // })
        }

        // Member 4 plugs Landlord Assignment/Priority details here
        composable(
            route = Screen.ComplaintDetail.route,
            arguments = listOf(navArgument("complaintId") { type = NavType.IntType })
        ) { backStackEntry ->
            val complaintId = backStackEntry.arguments?.getInt("complaintId") ?: 0
            PlaceholderScreen("Complaint Detail #$complaintId (Member 4)")
            // ComplaintDetailScreen(complaintId = complaintId, onBack = { navController.popBackStack() })
        }

        // Member 5 plugs Technician Task & Completion Evidence screen here
        composable(Screen.TechnicianTasks.route) {
            PlaceholderScreen("Technician Tasks (Member 5)")
            // TechnicianTasksScreen(...)
        }

        // Member 6 plugs Historical Maintenance Reports screen here
        composable(Screen.Reports.route) {
            PlaceholderScreen("Maintenance Reports (Member 6)")
            // ReportsScreen(...)
        }
    }
}

@Composable
private fun PlaceholderScreen(label: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = label)
    }
}