package com.example.propcare.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.propcare.PropCareApplication
import com.example.propcare.domain.model.UserRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    currentRole: UserRole,
    onRoleChange: (UserRole) -> Unit,
    onNavigateToSubmit: () -> Unit,
    onNavigateToComplaints: () -> Unit,
    onNavigateToTechTasks: () -> Unit,
    onNavigateToReports: () -> Unit,
    // Optional overrides (kept so existing callers still compile). Null = use live Room counts.
    totalComplaints: Int? = null,
    pendingComplaints: Int? = null,
    completedComplaints: Int? = null
) {
    // Live KPI counts from Room via PropCareRepository
    val repository = (LocalContext.current.applicationContext as PropCareApplication).container.repository
    val liveTotal by remember(repository) { repository.getTotalComplaintCount() }.collectAsState(initial = 0)
    val livePending by remember(repository) { repository.getPendingComplaintCount() }.collectAsState(initial = 0)
    val liveCompleted by remember(repository) { repository.getCompletedComplaintCount() }.collectAsState(initial = 0)

    val total = totalComplaints ?: liveTotal
    val pending = pendingComplaints ?: livePending
    val completed = completedComplaints ?: liveCompleted

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PropCare Dashboard", fontWeight = FontWeight.Bold) }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Role Selector Chips
            Text(
                text = "Active Role",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                UserRole.entries.forEach { role ->
                    FilterChip(
                        selected = currentRole == role,
                        onClick = { onRoleChange(role) },
                        label = { Text(role.name.lowercase().replaceFirstChar { it.uppercase() }) }
                    )
                }
            }

            // 2. Maintenance KPI Summary Cards
            Text(
                text = "Maintenance Overview",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                KpiCard(title = "Total", count = total, modifier = Modifier.weight(1f))
                KpiCard(title = "Pending", count = pending, modifier = Modifier.weight(1f))
                KpiCard(title = "Completed", count = completed, modifier = Modifier.weight(1f))
            }

            // 3. Role-Specific Quick Action Cards
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            when (currentRole) {
                UserRole.TENANT -> {
                    ActionCard(
                        title = "Submit New Maintenance Complaint",
                        subtitle = "Report an issue and attach photo evidence",
                        onClick = onNavigateToSubmit
                    )
                    ActionCard(
                        title = "Track My Complaints",
                        subtitle = "Check repair progress and technician notes",
                        onClick = onNavigateToComplaints
                    )
                }

                UserRole.LANDLORD -> {
                    ActionCard(
                        title = "Review & Assign Incoming Complaints",
                        subtitle = "Set priority levels and assign maintenance staff",
                        onClick = onNavigateToComplaints
                    )
                    ActionCard(
                        title = "View Recurring Issue Reports",
                        subtitle = "Analyze property maintenance history and trends",
                        onClick = onNavigateToReports
                    )
                }

                UserRole.TECHNICIAN -> {
                    ActionCard(
                        title = "View Assigned Repair Tasks",
                        subtitle = "Inspect pending work orders and site details",
                        onClick = onNavigateToTechTasks
                    )
                    ActionCard(
                        title = "Submit Completion Evidence",
                        subtitle = "Upload repair photos and mark jobs completed",
                        onClick = onNavigateToTechTasks
                    )
                }
            }
        }
    }
}

@Composable
private fun KpiCard(
    title: String,
    count: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
private fun ActionCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
