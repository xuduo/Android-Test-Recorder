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

package com.xd.dummyapp.weather

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.xd.common.widget.AppBar
import com.xd.common.widget.RefreshingLoadingContent
import com.xd.dummyapp.R
import com.xd.dummyapp.model.weather.CachedWeatherViewModel

@Composable
fun CachedWeatherScreen(
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
        WeatherContent(
            modifier = Modifier
                .padding(it)
        )
    }
}

@Composable
private fun WeatherContent(
    viewModel: CachedWeatherViewModel = hiltViewModel(),
    modifier: Modifier,
) {
    val data by viewModel.weather.observeAsState()
    RefreshingLoadingContent(
        data,
        onRefresh = { viewModel.fetchWeather() }
    ) {
        LazyColumn {
            itemsIndexed(it.hourly.temperature) { index, _ ->
                Item(
                    temperature = it.hourly.temperature[index],
                    dateTime = it.hourly.time[index]
                )
            }
        }
    }
}


