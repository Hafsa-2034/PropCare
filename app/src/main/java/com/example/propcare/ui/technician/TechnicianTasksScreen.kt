
package com.example.propcare.ui.technician
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.Text
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.propcare.data.local.ComplaintEntity

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("unused")
@Composable
fun TechnicianTasksScreen(
    viewModel: TechnicianViewModel,
    onNavigateBack: () -> Unit
) {
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val weather by viewModel.weather.collectAsStateWithLifecycle()
    val weatherError by viewModel.weatherError.collectAsStateWithLifecycle()
    val loading by viewModel.loading.collectAsStateWithLifecycle()

    var selectedTask by remember { mutableStateOf<ComplaintEntity?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Technician Tasks") },
                navigationIcon = {
                    TextButton(onClick = onNavigateBack) {
                        Text("Back")
                    }
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Weather Advisory", style = MaterialTheme.typography.titleLarge)

                        when {
                            loading -> CircularProgressIndicator()
                            weather != null -> {
                                Text("Temperature: ${weather!!.temperature} °C")
                                Text("Wind speed: ${weather!!.windSpeed} km/h")
                                Text("Tip: Check weather conditions before outdoor repairs.")
                            }
                            else -> Text(weatherError ?: "Weather unavailable")
                        }

                        TextButton(onClick = { viewModel.loadWeather() }) {
                            Text("Refresh Weather")
                        }
                    }
                }
            }

            item {
                Text("My Assigned Tasks", style = MaterialTheme.typography.titleLarge)
            }

            if (tasks.isEmpty()) {
                item { Text("No assigned tasks available.") }
            }

            items(tasks, key = { it.id }) { task ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(task.title, style = MaterialTheme.typography.titleMedium)
                        Text("Unit: ${task.unitNumber}")
                        Text("Category: ${task.category.label}")
                        Text("Priority: ${task.priority.label}")
                        Text("Status: ${task.status.label}")
                        Text(task.description)

                        if (task.status.name == "ASSIGNED") {
                            Button(onClick = {
                                viewModel.markInProgress(task)
                            }) {
                                Text("Start Task")
                            }
                        }

                        Button(onClick = {
                            selectedTask = task
                        }) {
                            Text("Complete Task")
                        }
                    }
                }
            }
        }
    }

    selectedTask?.let { task ->
        CompletionDialog(
            task = task,
            onDismiss = { selectedTask = null },
            onComplete = { notes, photo ->
                viewModel.markCompleted(task, notes, photo)
                selectedTask = null
            }
        )
    }
}

@Composable
private fun CompletionDialog(
    task: ComplaintEntity,
    onDismiss: () -> Unit,
    onComplete: (String, String?) -> Unit
) {
    var notes by remember { mutableStateOf(task.technicianNotes ?: "") }
    var photoUri by remember { mutableStateOf<String?>(null) }

    val photoPicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        photoUri = uri?.toString()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Complete Maintenance Task") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Technician Notes") }
                )

                Button(onClick = {
                    photoPicker.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                }) {
                    Text("Attach Completion Photo")
                }

                photoUri?.let {
                    AsyncImage(
                        model = it,
                        contentDescription = "Completion evidence",
                        modifier = Modifier.size(120.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onComplete(notes, photoUri) },
                enabled = notes.isNotBlank()
            ) {
                Text("Mark Completed")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
@Preview(showBackground = true)
@Composable
fun TechnicianTasksPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Technician Tasks",
                style = MaterialTheme.typography.headlineMedium
            )

            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Weather Advisory")
                    Text("Temperature: 25 °C")
                    Text("Wind speed: 12 km/h")
                    Text("Check weather before outdoor repairs.")
                }
            }

            Text(
                "My Assigned Tasks",
                style = MaterialTheme.typography.titleLarge
            )

            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Leaking Kitchen Tap")
                    Text("Unit: A102")
                    Text("Category: Plumbing")
                    Text("Priority: High")
                    Text("Status: Assigned")

                    Button(onClick = {}) {
                        Text("Start Task")
                    }

                    Button(onClick = {}) {
                        Text("Complete Task")
                    }
                }
            }
        }
    }
}