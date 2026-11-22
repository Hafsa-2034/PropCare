package com.example.propcare.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.propcare.data.repository.PropCareRepository
import com.example.propcare.domain.model.ComplaintStatus
import com.example.propcare.domain.model.IssueCategory
import com.example.propcare.utils.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    repository: PropCareRepository,
    onNavigateBack: () -> Unit
) {
    val complaintsFlow = remember(repository) { repository.getAllComplaints() }
    val complaints by complaintsFlow.collectAsState(initial = emptyList())

    val total = complaints.size
    val resolved = complaints.count { it.status == ComplaintStatus.RESOLVED }
    val rate = if (total == 0) 0 else resolved * 100 / total
    val byCategory = IssueCategory.values().associateWith { c -> complaints.count { it.category == c } }
    val overdue = complaints.filter { DateUtils.isOverdue(it.createdAt, it.status) }
    // "Recurring" = same unit + category reported 2+ times
    val recurring = complaints.groupBy { it.unitNumber to it.category }
        .filter { it.value.size >= 2 }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Maintenance Reports") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Resolution Rate", style = MaterialTheme.typography.titleMedium)
                        Text("$rate%", style = MaterialTheme.typography.displaySmall)
                        Text("$resolved of $total complaints resolved")
                        Spacer(Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { if (total == 0) 0f else resolved / total.toFloat() },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            item {
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("By Category", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        byCategory.forEach { (cat, count) ->
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(cat.name)
                                Text("$count")
                            }
                        }
                    }
                }
            }
            item { Text("Overdue (48h+) — ${overdue.size}", style = MaterialTheme.typography.titleMedium) }
            if (overdue.isEmpty()) item { Text("No overdue complaints 🎉") }
            items(overdue, key = { it.id }) { c ->
                ListItem(
                    headlineContent = { Text(c.title) },
                    supportingContent = {
                        Text("Unit ${c.unitNumber} • ${c.category.name} • open ${DateUtils.hoursOpen(c.createdAt)}h")
                    },
                    trailingContent = { Text(c.priority.name) }
                )
            }
            item { Text("Recurring Issues — ${recurring.size}", style = MaterialTheme.typography.titleMedium) }
            if (recurring.isEmpty()) item { Text("No recurring issues") }
            items(recurring.entries.toList(), key = { "${it.key.first}-${it.key.second}" }) { (key, list) ->
                ListItem(
                    headlineContent = { Text("Unit ${key.first} — ${key.second.name}") },
                    supportingContent = { Text("Reported ${list.size} times") }
                )
            }
        }
    }
}
