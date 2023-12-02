package com.xd.mvvm.boilerplate.recording

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import com.xd.mvvm.boilerplate.data.Recording
import com.xd.mvvm.boilerplate.logger.L
import com.xd.mvvm.boilerplate.overlay.OverlayService
import com.xd.mvvm.boilerplate.util.DataLoadingContent
import com.xd.mvvm.boilerplate.widget.AppBar

@Composable
fun RecordingListScreen(
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
        RecordingListScreenContent(
            modifier = Modifier.padding(it)
        )
    }
}

@Composable
private fun RecordingListScreenContent(
    viewModel: RecordingViewModel = hiltViewModel(),
    modifier: Modifier,
) {
    val data by viewModel.getAllRecordings().observeAsState()
    L.d("RecordingViewModel.getAppsSortedByRecentUsage() ${data?.value}")
    DataLoadingContent(
        data,
        onRefresh = { viewModel.getAllRecordings() }
    ) {
        LazyColumn {
            L.d("WeatherContent RecordingListScreenContent ${it.size}")
            items(it.size) { index ->
                Item(
                    it[index]
                )
            }
        }
    }
}

@Composable
private fun Item(
    recording: Recording
) {
    val context = LocalContext.current

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
            }
            .padding(
                horizontal = dimensionResource(id = R.dimen.horizontal_margin),
                vertical = dimensionResource(id = R.dimen.list_item_padding),
            )
    ) {
        recording.getIconBitmap()?.let {
            Image(
                bitmap = it,
                contentDescription = "App Icon",
                modifier = Modifier
                    .padding(end = dimensionResource(id = R.dimen.horizontal_margin))
                    .size(40.dp)
            )
            Text(
                text = recording.name ?: "",
                style = MaterialTheme.typography.h6
            )
        }
    }
}

private fun startApp(viewModel: RecordingViewModel, context: Context, appInfo: AppInfo) {
    val intent = context.packageManager.getLaunchIntentForPackage(appInfo.packageName)
    if (intent != null) {
        // If there is a launchable activity, start it
        viewModel.startRecording(appInfo)
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
