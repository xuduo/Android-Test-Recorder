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

package com.xd.testrecorder.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.xd.testrecorder.LocalNavController
import com.xd.testrecorder.MainDestinations
import com.xd.testrecorder.R
import com.xd.testrecorder.accessibility.TouchAccessibilityViewModel
import com.xd.testrecorder.widget.AppBar

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    touchAccessibilityViewModel: TouchAccessibilityViewModel = hiltViewModel(),
) {
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            AppBar(titleId = R.string.app_name, showBackButton = false)
        },
        modifier = modifier.fillMaxSize()
    )
    {
        val context = LocalContext.current
        val nav = LocalNavController.current
        Column {
            Modifier.padding(it)
            MainScreenItem(text = "Choose App and Record") {
                nav.navigate(MainDestinations.PROCESS_LIST)
            }
            MainScreenItem(text = "Recording List") {
                nav.navigate(MainDestinations.RECORDING_LIST)
            }
        }
    }
}

@Preview
@Composable
private fun MainScreenItem(
    text: String = "default",
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .semantics { contentDescription = text }
            .fillMaxWidth()
            .clickable(onClick = onClick) // use the passed lambda here
            .padding(16.dp)
    ) {
        Text(text)
    }
}
