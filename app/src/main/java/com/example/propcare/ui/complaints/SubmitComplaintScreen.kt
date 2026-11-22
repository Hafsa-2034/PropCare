package com.example.propcare.ui.complaints

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.propcare.PropCareApplication
import com.example.propcare.domain.model.IssueCategory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmitComplaintScreen(onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    val repository = (context.applicationContext as PropCareApplication).container.repository
    val viewModel: ComplaintViewModel = viewModel(factory = ComplaintViewModel.factory(repository))

    val form by viewModel.form.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var categoryMenuOpen by remember { mutableStateOf(false) }

    // Photo picker. takePersistableUriPermission keeps access after the app restarts.
    val photoLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: SecurityException) {
                // Some devices refuse persistable access; the photo still works for this session
            }
            viewModel.onPhotoSelected(uri.toString())
        }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is ComplaintEvent.Saved -> {
                    Toast.makeText(context, "Complaint submitted", Toast.LENGTH_SHORT).show()
                    onNavigateBack()
                }
                is ComplaintEvent.Failed -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Report an Issue") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = form.title,
                onValueChange = viewModel::onTitleChange,
                label = { Text("Title") },
                isError = form.titleError != null,
                supportingText = { form.titleError?.let { Text(it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = form.unitNumber,
                onValueChange = viewModel::onUnitChange,
                label = { Text("Unit / Room number (e.g. A101)") },
                isError = form.unitError != null,
                supportingText = { form.unitError?.let { Text(it) } },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Box {
                OutlinedButton(
                    onClick = { categoryMenuOpen = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Category: ${form.category.label}")
                }
                DropdownMenu(
                    expanded = categoryMenuOpen,
                    onDismissRequest = { categoryMenuOpen = false }
                ) {
                    IssueCategory.entries.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.label) },
                            onClick = {
                                viewModel.onCategoryChange(category)
                                categoryMenuOpen = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = form.description,
                onValueChange = viewModel::onDescriptionChange,
                label = { Text("Describe the problem") },
                isError = form.descriptionError != null,
                supportingText = { form.descriptionError?.let { Text(it) } },
                minLines = 4,
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = {
                        photoLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
                ) {
                    Text(if (form.photoUri == null) "Attach Photo" else "Change Photo")
                }
                if (form.photoUri != null) {
                    OutlinedButton(onClick = { viewModel.onPhotoSelected(null) }) {
                        Text("Remove")
                    }
                }
            }

            if (form.photoUri != null) {
                AsyncImage(
                    model = form.photoUri,
                    contentDescription = "Attached photo preview",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
            }

            Button(
                onClick = viewModel::submit,
                enabled = !form.isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (form.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Text("Submit Complaint")
                }
            }
        }
    }
}