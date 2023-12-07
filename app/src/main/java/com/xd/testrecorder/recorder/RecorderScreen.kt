/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xd.testrecorder.recorder

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.xd.testrecorder.R
import com.xd.testrecorder.widget.AppBar

@Composable
fun RecorderScreen(
    modifier: Modifier = Modifier,
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {
    Scaffold(
        scaffoldState = scaffoldState,
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppBar(R.string.weather)
        },
    ) {
        RecorderScreenContent(modifier = Modifier
            .padding(it))
    }
}

@Composable
private fun RecorderScreenContent(
    viewModel: RecorderViewModel = hiltViewModel(),
    modifier: Modifier,
) {

    val startForResult = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            viewModel.handleScreenCaptureResult(result.resultCode, result.data)
        } else {
            // Permission denied
        }
    }
    Button(onClick = {
        viewModel.startCapture(startForResult)
         }) {
        Text("Start Screen Recording")
    }

}


