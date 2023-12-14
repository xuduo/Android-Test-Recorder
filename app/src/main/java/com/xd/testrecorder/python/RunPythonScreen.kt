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

package com.xd.testrecorder.python

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.xd.common.widget.AppBar
import com.xd.testrecorder.R

@Composable
fun RunPythonScreen(
    modifier: Modifier = Modifier,
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {
    Scaffold(
        scaffoldState = scaffoldState,
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppBar(R.string.record_screen)
        },
    ) {
        RecorderScreenContent(
            modifier = Modifier
                .padding(it)
        )
    }
}

@Composable
private fun RecorderScreenContent(
    viewModel: PythonViewModel = hiltViewModel(),
    modifier: Modifier,
) {
    var text by remember { mutableStateOf("") }
    val result by viewModel.result.observeAsState("")
    Column {
        TextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            maxLines = 10,  // Adjust the number of lines as needed
            label = { Text("Enter text") }
        )
        Button(onClick = {
            viewModel.runCode("startForResult")
        }) {
            Text("Run Python Code")
        }
        Text(text = result)
    }

}


