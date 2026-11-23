package com.example.propcare.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.propcare.PropCareApplication
import com.example.propcare.domain.model.UserRole
import com.example.propcare.ui.complaints.ComplaintListScreen
import com.example.propcare.ui.complaints.SubmitComplaintScreen
import com.example.propcare.ui.dashboard.DashboardScreen
import com.example.propcare.ui.dashboard.ReportsScreen
import com.example.propcare.ui.manager.ComplaintDetailScreen
import com.example.propcare.ui.technician.TechnicianTasksScreen
import com.example.propcare.ui.technician.TechnicianViewModel

@Composable
fun PropCareNavGraph(
    navController: NavHostController,
    currentRole: UserRole,
    onRoleChange: (UserRole) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route,
        modifier = modifier,
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                currentRole = currentRole,
                onRoleChange = onRoleChange,
                onNavigateToSubmit = { navController.navigate(Screen.SubmitComplaint.route) },
                onNavigateToComplaints = { navController.navigate(Screen.ComplaintList.route) },
                onNavigateToTechTasks = { navController.navigate(Screen.TechnicianTasks.route) },
                onNavigateToReports = { navController.navigate(Screen.Reports.route) },
            )
        }

        // Member 3 plugs SubmitComplaintScreen & ComplaintListScreen here
        composable(Screen.SubmitComplaint.route) {
            SubmitComplaintScreen { navController.popBackStack() }
        }

        composable(Screen.ComplaintList.route) {
            ComplaintListScreen(
                onComplaintClick = { id ->
                    navController.navigate(Screen.ComplaintDetail.createRoute(id))
                },
                onNavigateBack = { navController.popBackStack() },
            )
        }

        // Member 4 plugs Landlord Assignment/Priority details here
        composable(
            route = Screen.ComplaintDetail.route,
            arguments = listOf(navArgument("complaintId") { type = NavType.IntType }),
        ) { backStackEntry ->
            val complaintId = backStackEntry.arguments?.getInt("complaintId") ?: 0
            ComplaintDetailScreen(complaintId = complaintId) {
                navController.popBackStack()
            }
        }

        composable(Screen.TechnicianTasks.route) {
            val container = (LocalContext.current.applicationContext as PropCareApplication).container

            val viewModel = remember {
                TechnicianViewModel(
                    repository = container.repository,
                    technicianId = 1,
                )
            }

            TechnicianTasksScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
            )
        }

        // Member 6 plugs Historical Maintenance Reports screen here
        composable(Screen.Reports.route) {
            val container = (LocalContext.current.applicationContext as PropCareApplication).container
            ReportsScreen(
                repository = container.repository,
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}

@Suppress("unused")
@Composable
private fun PlaceholderScreen(label: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = label)
    }
}
