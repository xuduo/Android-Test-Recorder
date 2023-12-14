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

package com.xd.dummyapp.time

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.xd.common.widget.AppBar
import com.xd.dummyapp.R
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun TimeScreen(
    modifier: Modifier = Modifier,
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {
    Scaffold(
        scaffoldState = scaffoldState,
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppBar(R.string.cached_weather)
        },
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            TimeContent(
                modifier = Modifier
                    .padding(it)
            )
        }
    }
}

@Composable
private fun TimeContent(
    modifier: Modifier,
) {
    var currentTime by remember { mutableStateOf(getCurrentTimeString()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = getCurrentTimeString()
            delay(10) // Update every 100 milliseconds
        }
    }

    Text(
        text = "Current Time: $currentTime",
        style = TextStyle(fontSize = 30.sp) // Making the text larger
    )
}

fun getCurrentTimeString(): String {
    val currentTime = Calendar.getInstance().time
    val formatter = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())
    return formatter.format(currentTime)
}




