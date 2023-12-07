package com.xd.dummyapp.weather

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.xd.dummyapp.LocalLogger
import com.xd.dummyapp.R
import com.xd.dummyapp.model.weather.WeatherViewModel
import com.xd.dummyapp.util.RefreshingLoadingContent
import com.xd.dummyapp.widget.AppBar

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
    val logger = LocalLogger.current
    logger.d("WeatherContent compose")
    RefreshingLoadingContent(
        data,
        onRefresh = { viewModel.fetchWeather() }
    ) {
        LazyColumn {
            logger.d("WeatherContent LazyColumn ${it.hourly.temperature.size}")
            items(it.hourly.temperature.size){index->
                Item(
                    temperature = it.hourly.temperature[index],
                    dateTime = it.hourly.time[index]
                )
            }
        }
    }
}



