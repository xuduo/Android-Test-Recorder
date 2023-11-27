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

package com.xd.mvvm.boilerplate.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Checkbox
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.xd.mvvm.boilerplate.MainDestinations
import com.xd.mvvm.boilerplate.R
import com.xd.mvvm.boilerplate.NavigationActions
import com.xd.mvvm.boilerplate.config.ConfigViewModel
import com.xd.mvvm.boilerplate.widget.AppBar

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    navActions: NavigationActions
) {
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            AppBar(titleId = R.string.app_name, showBackButton = false)
        },
        modifier = modifier.fillMaxSize()
    )
    {
        Column {
            Modifier.padding(it)
            MainScreenItem(text = stringResource(R.string.enter_weather_label)) {
                navActions.navigate(MainDestinations.WEATHER)
            }
            MainScreenItem(text = stringResource(id = R.string.enter_weather_cache_label)) {
                navActions.navigate(MainDestinations.CACHED_WEATHER)
            }
            SimulateErrorConfig()
            SimulateHttpLatencyConfig()
            MainScreenItem(text = stringResource(R.string.record_screen)) {
                navActions.navigate(MainDestinations.RECORD)
            }
        }
    }
}

@Composable
fun SimulateErrorConfig(viewModel: ConfigViewModel = hiltViewModel()) {
    // Observe the LiveData
    val simulateError by viewModel.simulateNetworkError.observeAsState(initial = false)

    // Layout
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically // This will align the Text and Checkbox vertically in the center
    ) {
        Text(
            text = stringResource(id = R.string.simulate_http_error),
            modifier = Modifier.weight(1f) // This will make the Text occupy all available space
        )

        Switch(
            checked = simulateError,
            onCheckedChange = {
                viewModel.simulateNetworkError.toggle() // Toggle the value when checkbox is clicked
            },
            modifier = Modifier.testTag(stringResource(R.string.simulate_http_error))
        )
    }

}

@Composable
fun SimulateHttpLatencyConfig(viewModel: ConfigViewModel = hiltViewModel()) {
    // Observe the LiveData
    val simulateError by viewModel.simulateHttpLatency.observeAsState(initial = false)

    // Layout
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically // This will align the Text and Checkbox vertically in the center
    ) {
        Text(
            text = "Simulate HTTP Latency 3000 milli seconds:",
            modifier = Modifier.weight(1f) // This will make the Text occupy all available space
        )

        Checkbox(
            checked = simulateError,
            onCheckedChange = {
                viewModel.simulateHttpLatency.toggle() // Toggle the value when checkbox is clicked
            }
        )
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
            .fillMaxWidth()
            .clickable(onClick = onClick) // use the passed lambda here
            .padding(16.dp)
    ) {
        Text(text)
    }
}
