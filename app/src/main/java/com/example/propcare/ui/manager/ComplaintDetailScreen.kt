package com.example.propcare.ui.manager

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.propcare.PropCareApplication
import com.example.propcare.domain.model.ComplaintStatus
import com.example.propcare.domain.model.PriorityLevel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComplaintDetailScreen(complaintId: Int, onBack: () -> Unit) {
    val context = LocalContext.current
    val container = (context.applicationContext as PropCareApplication).container
    val viewModel: ManagerViewModel = viewModel(
        factory = ManagerViewModel.factory(container.repository, complaintId)
    )

    val complaint by viewModel.complaint.collectAsState()
    val technicians by viewModel.technicians.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Show confirmation messages from the ViewModel
    LaunchedEffect(Unit) {
        viewModel.messages.collect { snackbarHostState.showSnackbar(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Complaint Details") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        val c = complaint
        if (c == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }
        var techMenuOpen by remember { mutableStateOf(false) }
        val assignedName = technicians.find { it.id == c.assignedTechnicianId }?.name

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ---- Complaint info ----
            Text(c.title, style = MaterialTheme.typography.headlineSmall)
            Text(c.description, style = MaterialTheme.typography.bodyLarge)
            Text("Unit: ${c.unitNumber}")
            Text("Category: ${c.category.name}")
            Text("Status: ${c.status.name}", style = MaterialTheme.typography.titleSmall)
            Text("Created: ${dateFormat.format(Date(c.createdAt))}")
            Text("Updated: ${dateFormat.format(Date(c.updatedAt))}")

            // ---- Photos ----
            c.photoUri?.let {
                Text("Tenant photo", style = MaterialTheme.typography.titleSmall)
                AsyncImage(
                    model = it,
                    contentDescription = "Tenant photo",
                    modifier = Modifier.fillMaxWidth().heightIn(max = 280.dp),
                    contentScale = ContentScale.Crop
                )
            }
            if (c.status == ComplaintStatus.RESOLVED) {
                c.completionPhotoUri?.let {
                    Text("Completion photo", style = MaterialTheme.typography.titleSmall)
                    AsyncImage(
                        model = it,
                        contentDescription = "Completion photo",
                        modifier = Modifier.fillMaxWidth().heightIn(max = 280.dp),
                        contentScale = ContentScale.Crop
                    )
                }
                c.technicianNotes?.let { Text("Technician notes: $it") }
            }

            HorizontalDivider()

            // ---- Landlord controls: priority ----
            Text("Priority", style = MaterialTheme.typography.titleMedium)
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PriorityLevel.values().forEach { level ->
                    FilterChip(
                        selected = c.priority == level,
                        onClick = { viewModel.updatePriority(level) },
                        label = { Text(level.name) }
                    )
                }
            }

            // ---- Landlord controls: technician ----
            Text("Assign technician", style = MaterialTheme.typography.titleMedium)
            ExposedDropdownMenuBox(
                expanded = techMenuOpen,
                onExpandedChange = { techMenuOpen = it }
            ) {
                OutlinedTextField(
                    value = assignedName ?: "Select technician",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(techMenuOpen) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = techMenuOpen,
                    onDismissRequest = { techMenuOpen = false }
                ) {
                    technicians.forEach { tech ->
                        DropdownMenuItem(
                            text = { Text("${tech.name} (${tech.unitOrSpecialty})") },
                            onClick = {
                                viewModel.assignTechnician(tech)
                                techMenuOpen = false
                            }
                        )
                    }
                }
            }

            // ---- Landlord controls: status ----
            Text("Update status", style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { viewModel.updateStatus(ComplaintStatus.ASSIGNED) },
                    enabled = c.status != ComplaintStatus.RESOLVED
                ) { Text("Mark Assigned") }
                Button(
                    onClick = { viewModel.updateStatus(ComplaintStatus.RESOLVED) },
                    enabled = c.status != ComplaintStatus.RESOLVED
                ) { Text("Mark Resolved") }
            }
        }
    }
}

