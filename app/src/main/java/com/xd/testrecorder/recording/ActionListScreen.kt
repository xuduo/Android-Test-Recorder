package com.xd.testrecorder.recording

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.xd.testrecorder.LocalNavController
import com.xd.testrecorder.R
import com.xd.testrecorder.data.Action
import com.xd.testrecorder.goToActionImage
import com.xd.testrecorder.logger.L
import com.xd.testrecorder.recording.RecordingViewModel
import com.xd.testrecorder.util.LiveDataLoadingContent
import com.xd.testrecorder.widget.AppBar

@Composable
fun ActionListScreen(
    recordingId: Long,
    modifier: Modifier = Modifier,
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {
    Scaffold(
        scaffoldState = scaffoldState,
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppBar(R.string.action_list) // Change the string resource to your app's name
        },
    ) {
        ActionListScreenContent(
            recordingId,
            modifier = Modifier.padding(it)
        )
    }
}

@Composable
private fun ActionListScreenContent(
    recordingId: Long,
    viewModel: RecordingViewModel = hiltViewModel(),
    modifier: Modifier,
) {
    val data = viewModel.getActionsByRecordingId(recordingId)
    L.d("RecordingViewModel.getActionsByRecordingId() ${data?.value}")
    LiveDataLoadingContent(
        data
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
    action: Action
) {
    val nav = LocalNavController.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                nav.goToActionImage(action.recordingId, action.id)
            }
            .padding(
                horizontal = dimensionResource(id = R.dimen.horizontal_margin),
                vertical = dimensionResource(id = R.dimen.list_item_padding),
            )
    ) {
        Text(
            text = action.type ?: "",
            style = MaterialTheme.typography.h6
        )
        Text(
            text = action.duration.toString() ?: "",
            style = MaterialTheme.typography.h6
        )
    }
}