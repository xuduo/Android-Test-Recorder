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

package com.xd.mvvm.boilerplate.weather

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.xd.mvvm.boilerplate.R
import com.xd.mvvm.boilerplate.logger.L
import com.xd.mvvm.boilerplate.util.DataLoadingContent
import com.xd.mvvm.boilerplate.widget.AppBar
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun WeatherScreen(
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
        WeatherContent(modifier = Modifier
            .padding(it))
    }
}

@Composable
private fun WeatherContent(
    viewModel: WeatherViewModel = hiltViewModel(),
    modifier: Modifier,
) {
    val data by viewModel.weather.observeAsState()
    L.d("WeatherContent compose")
    DataLoadingContent(
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

@Composable
private fun Item(
    temperature: String,
    dateTime: LocalDateTime
) {
    val context = LocalContext.current
    val formatter = DateTimeFormatter.ofPattern("EEEE h a")
    val temperatureText = "$temperature°C"
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                Toast
                    .makeText(context, temperatureText, Toast.LENGTH_SHORT)
                    .show()
            }
            .padding(
                horizontal = dimensionResource(id = R.dimen.horizontal_margin),
                vertical = dimensionResource(id = R.dimen.list_item_padding),
            )
    ) {
        Text(
            text = dateTime.format(formatter),
            style = MaterialTheme.typography.h6,
            modifier = Modifier
                .weight(1f) // This is the key change
                .padding(start = dimensionResource(id = R.dimen.horizontal_margin))
        )
        Text(
            text = temperatureText,
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(
                start = dimensionResource(id = R.dimen.horizontal_margin)
            )
        )
    }
}


