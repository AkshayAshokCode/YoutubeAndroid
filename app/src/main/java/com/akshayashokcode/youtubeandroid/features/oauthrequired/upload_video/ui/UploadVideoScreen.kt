package com.akshayashokcode.youtubeandroid.features.oauthrequired.upload_video.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.akshayashokcode.youtubeandroid.core.ui.UiState
import com.akshayashokcode.youtubeandroid.features.oauthrequired.upload_video.VideoUploadViewModel
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadVideoScreen(
    onBackClick: () -> Unit,
    viewModel: VideoUploadViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var tags by rememberSaveable { mutableStateOf("") }
    var categoryId by rememberSaveable { mutableStateOf("22") }
    var privacyExpanded by rememberSaveable { mutableStateOf(false) }
    var selectedPrivacy by rememberSaveable { mutableStateOf("private") }
    var madeForKids by rememberSaveable { mutableStateOf(false) }
    var selectedFileName by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedFile by rememberSaveable { mutableStateOf<String?>(null) }

    val uiState by viewModel.uiState.collectAsState()
    val privacyOptions = listOf("private", "public", "unlisted")

    val filePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val fileName = it.lastPathSegment ?: "upload.mp4"
            selectedFileName = fileName
            val tempFile = File(context.cacheDir, fileName)
            context.contentResolver.openInputStream(it)?.use { input ->
                FileOutputStream(tempFile).use { out -> input.copyTo(out) }
            }
            selectedFile = tempFile.absolutePath
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item { OutlinedButton(onClick = onBackClick) { Text("Back") } }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Upload Video", style = MaterialTheme.typography.headlineMedium)
                Text("Endpoint: videos.insert (multipart)\nScope: youtube.upload · Quota: 1600 units",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        item {
            OutlinedButton(onClick = { filePicker.launch("video/*") },
                modifier = Modifier.fillMaxWidth()) {
                Text(selectedFileName ?: "Pick video file")
            }
        }
        item {
            OutlinedTextField(value = title, onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth(), label = { Text("Title") })
        }
        item {
            OutlinedTextField(value = description, onValueChange = { description = it },
                modifier = Modifier.fillMaxWidth(), label = { Text("Description") }, minLines = 3)
        }
        item {
            OutlinedTextField(value = tags, onValueChange = { tags = it },
                modifier = Modifier.fillMaxWidth(), label = { Text("Tags (comma-separated)") })
        }
        item {
            OutlinedTextField(value = categoryId, onValueChange = { categoryId = it },
                modifier = Modifier.fillMaxWidth(), label = { Text("Category ID") },
                placeholder = { Text("22 = People & Blogs") })
        }
        item {
            ExposedDropdownMenuBox(expanded = privacyExpanded,
                onExpandedChange = { privacyExpanded = !privacyExpanded }) {
                OutlinedTextField(
                    value = selectedPrivacy, onValueChange = {}, readOnly = true,
                    label = { Text("Privacy") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(privacyExpanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                )
                ExposedDropdownMenu(expanded = privacyExpanded,
                    onDismissRequest = { privacyExpanded = false }) {
                    privacyOptions.forEach { opt ->
                        DropdownMenuItem(text = { Text(opt) },
                            onClick = { selectedPrivacy = opt; privacyExpanded = false })
                    }
                }
            }
        }
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = madeForKids, onCheckedChange = { madeForKids = it })
                Text("Made for kids", style = MaterialTheme.typography.bodyMedium)
            }
        }
        item {
            Button(
                onClick = {
                    selectedFile?.let {
                        viewModel.upload(File(it), title, description, tags, categoryId, selectedPrivacy, madeForKids)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedFile != null && uiState !is UiState.Loading,
            ) { Text("Upload") }
        }

        when (val s = uiState) {
            UiState.Loading -> item { CircularProgressIndicator() }
            is UiState.Data -> item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Upload complete!", color = MaterialTheme.colorScheme.primary)
                    s.value.id?.let { Text("Video ID: $it") }
                    s.value.snippet?.title?.let { Text("Title: $it") }
                    s.value.status?.privacyStatus?.let { Text("Privacy: $it") }
                }
            }
            is UiState.Error -> item { Text(s.message, color = MaterialTheme.colorScheme.error) }
            else -> Unit
        }
    }
}
