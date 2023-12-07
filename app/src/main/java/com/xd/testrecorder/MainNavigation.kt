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

package com.xd.testrecorder

import androidx.navigation.NavController
import com.xd.testrecorder.MainDestinations.ACTION_IMAGE
import com.xd.testrecorder.MainDestinations.ACTION_LIST

/**
 * Destinations used in the [MainActivity]
 */
object MainDestinations {
    const val RECORD = "RECORD"
    const val MAIN = "main"
    const val WEATHER = "weather"
    const val CACHED_WEATHER = "cached_weather"
    const val PROCESS_LIST = "process_list"
    const val RECORDING_LIST = "recording_list"
    const val ACTION_LIST = "action_list"
    const val ACTION_IMAGE = "action_image"
}

fun NavController.goToActionList(recordingId: Long) {
    this.navigate("$ACTION_LIST/$recordingId")
}

fun NavController.goToActionImage(recordingId: Long, actionId: Long) {
    this.navigate("$ACTION_IMAGE/$recordingId/$actionId")
}
