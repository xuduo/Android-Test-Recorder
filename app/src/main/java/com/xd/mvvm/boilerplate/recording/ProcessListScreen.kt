package com.xd.mvvm.boilerplate.recording

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.xd.mvvm.boilerplate.R
import com.xd.mvvm.boilerplate.logger.L
import com.xd.mvvm.boilerplate.overlay.OverlayService
import com.xd.mvvm.boilerplate.widget.AppBar

@Composable
fun ProcessListScreen(
    modifier: Modifier = Modifier,
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {
    Scaffold(
        scaffoldState = scaffoldState,
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppBar(R.string.choose_app_to_record) // Change the string resource to your app's name
        },
    ) {
        ProcessListScreenContent(
            modifier = Modifier.padding(it)
        )
    }
}

@Composable
private fun ProcessListScreenContent(
    viewModel: RecordingViewModel = hiltViewModel(),
    modifier: Modifier,
) {
    val data = viewModel.getAppsSortedByRecentUsage()
    L.d("RecordingViewModel.getAppsSortedByRecentUsage() ${data.size}")
    LazyColumn(modifier = modifier) {
        items(data) { appInfo ->
            AppItem(appInfo = appInfo)
        }
    }
}

@Composable
private fun AppItem(
    appInfo: AppInfo,
    viewModel: RecordingViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    var showAccessibilityDialog by remember { mutableStateOf(false) }
    if (showAccessibilityDialog) {
        AlertDialog(
            onDismissRequest = {
                showAccessibilityDialog = false
            }, // Hide dialog when user clicks outside of the dialog or presses the back button
            title = { Text("Accessibility Service") },
            text = { Text("You need to turn on Accessibility Service in system settings before recording.") },
            confirmButton = {
                Button(onClick = {
                    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                    context.startActivity(intent)
                    showAccessibilityDialog = false
                }) { // Hide dialog when confirm button is clicked
                    Text("Confirm")
                }
            },
            dismissButton = {
                Button(onClick = {
                    showAccessibilityDialog = false
                }) { // Hide dialog when dismiss button is clicked
                    Text("Dismiss")
                }
            }
        )
    }
    var showOverlayDialog by remember { mutableStateOf(false) }
    val startForResult = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            context.startService(Intent(context, OverlayService::class.java))
        } else {
            // Permission denied
        }
    }

    if (showOverlayDialog) {
        AlertDialog(
            onDismissRequest = {
                showOverlayDialog = false
            }, // Hide dialog when user clicks outside of the dialog or presses the back button
            title = { Text("Display Over Other APPs") },
            text = { Text("We need to turn on 'Display Over Other APPs' before recording.") },
            confirmButton = {
                Button(onClick = {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:${context.packageName}")
                    )
                    startForResult.launch(intent)
                    showOverlayDialog = false
                }) { // Hide dialog when confirm button is clicked
                    Text("Confirm")
                }
            },
            dismissButton = {
                Button(onClick = {
                    showOverlayDialog = false
                }) { // Hide dialog when dismiss button is clicked
                    Text("Dismiss")
                }
            }
        )
    }

    val startRecordForResult = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            viewModel.handleScreenCaptureResult(result.resultCode, result.data)
        } else {
            Toast
                .makeText(
                    context,
                    "Screencast permission not granted!",
                    Toast.LENGTH_LONG
                )
                .show()
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (!viewModel.isAccessibilityServiceEnabled()) {
                    showAccessibilityDialog = true
                    return@clickable
                }

                if (!viewModel.canShowOverlay()) {
                    showOverlayDialog = true
                    return@clickable
                }

                if (!viewModel.isRecording()) {
                    viewModel.startRecording(startRecordForResult)
                    return@clickable
                }

                val intent = context.packageManager.getLaunchIntentForPackage(appInfo.packageName)
                if (intent != null) {
                    // If there is a launchable activity, start it
                    context.startActivity(intent)
                } else {
                    // If there is no launchable activity, show a Toast or handle accordingly
                    Toast
                        .makeText(
                            context,
                            "No launchable activity found for ${appInfo.packageName}",
                            Toast.LENGTH_LONG
                        )
                        .show()
                }
            }
            .padding(
                horizontal = dimensionResource(id = R.dimen.horizontal_margin),
                vertical = dimensionResource(id = R.dimen.list_item_padding),
            )
    ) {
        Image(
            bitmap = appInfo.icon.asImageBitmap(),
            contentDescription = "App Icon",
            modifier = Modifier
                .padding(end = dimensionResource(id = R.dimen.horizontal_margin))
                .size(40.dp)
        )
        Text(
            text = appInfo.appName,
            style = MaterialTheme.typography.h6
        )
    }
}
