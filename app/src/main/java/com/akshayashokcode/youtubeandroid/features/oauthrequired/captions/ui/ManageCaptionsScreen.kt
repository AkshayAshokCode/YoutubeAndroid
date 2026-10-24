package com.akshayashokcode.youtubeandroid.features.oauthrequired.captions.ui

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.akshayashokcode.youtubeandroid.core.ui.UiState
import com.akshayashokcode.youtubeandroid.features.oauthrequired.captions.CaptionsViewModel
import java.io.File
import java.io.FileOutputStream

@Composable
fun ManageCaptionsScreen(
    onBackClick: () -> Unit,
    viewModel: CaptionsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    var videoId by rememberSaveable { mutableStateOf("") }
    var language by rememberSaveable { mutableStateOf("") }
    var captionName by rememberSaveable { mutableStateOf("") }
    var selectedFileName by rememberSaveable { mutableStateOf<String?>(null) }
    var selectedFile by rememberSaveable { mutableStateOf<String?>(null) }

    val state by viewModel.state.collectAsState()

    val filePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val fileName = it.lastPathSegment ?: "caption.vtt"
            selectedFileName = fileName
            val tempFile = File(context.cacheDir, fileName)
            context.contentResolver.openInputStream(it)?.use { input ->
                FileOutputStream(tempFile).use { output -> input.copyTo(output) }
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
                Text("Manage Captions", style = MaterialTheme.typography.headlineMedium)
                Text("Endpoints: captions.list · captions.insert · captions.delete\nScope: youtube.force-ssl · Quota: 50+ units",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        item {
            OutlinedTextField(
                value = videoId,
                onValueChange = { videoId = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Video ID") },
            )
        }

        item {
            Button(
                onClick = { viewModel.listCaptions(videoId) },
                modifier = Modifier.fillMaxWidth(),
                enabled = state.listState !is UiState.Loading,
            ) { Text("List Captions") }
        }

        when (val s = state.listState) {
            UiState.Loading -> item { CircularProgressIndicator() }
            is UiState.Error -> item { Text(s.message, color = MaterialTheme.colorScheme.error) }
            is UiState.Data -> {
                val caps = s.value.items
                if (caps.isEmpty()) {
                    item { Text("No captions found.") }
                } else {
                    items(caps) { cap ->
                        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                            Column(modifier = Modifier.padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(cap.snippet?.name ?: cap.snippet?.language ?: "—",
                                    fontWeight = FontWeight.SemiBold,
                                    style = MaterialTheme.typography.bodyMedium)
                                cap.snippet?.language?.let { Text("Language: $it") }
                                cap.snippet?.trackKind?.let { Text("Kind: $it") }
                                cap.id?.let { cid ->
                                    Button(
                                        onClick = { viewModel.deleteCaption(cid) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.errorContainer,
                                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
                                        ),
                                    ) { Text("Delete") }
                                }
                            }
                        }
                    }
                }
            }
            else -> Unit
        }

        item { Divider() }

        item {
            Text("Upload Caption Track", style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold)
        }

        item {
            OutlinedTextField(value = language, onValueChange = { language = it },
                modifier = Modifier.fillMaxWidth(), label = { Text("Language code (e.g. en)") })
        }
        item {
            OutlinedTextField(value = captionName, onValueChange = { captionName = it },
                modifier = Modifier.fillMaxWidth(), label = { Text("Track name") })
        }
        item {
            OutlinedButton(onClick = { filePicker.launch("*/*") },
                modifier = Modifier.fillMaxWidth()) {
                Text(selectedFileName ?: "Pick .vtt / .srt file")
            }
        }
        item {
            Button(
                onClick = {
                    selectedFile?.let {
                        viewModel.uploadCaption(videoId, language, captionName, File(it))
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedFile != null && state.uploadState !is UiState.Loading,
            ) { Text("Upload Caption") }
        }
        when (val s = state.uploadState) {
            UiState.Loading -> item { CircularProgressIndicator() }
            is UiState.Data -> item { Text("Caption uploaded!", color = MaterialTheme.colorScheme.primary) }
            is UiState.Error -> item { Text(s.message, color = MaterialTheme.colorScheme.error) }
            else -> Unit
        }
        when (val s = state.deleteState) {
            is UiState.Data -> item { Text("Caption deleted.", color = MaterialTheme.colorScheme.primary) }
            is UiState.Error -> item { Text(s.message, color = MaterialTheme.colorScheme.error) }
            else -> Unit
        }
    }
}
