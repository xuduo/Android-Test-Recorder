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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.xd.common.logger.Logger
import com.xd.common.nav.LocalLogger
import com.xd.common.nav.LocalNavController
import com.xd.testrecorder.main.MainScreen
import com.xd.testrecorder.python.RunPythonScreen
import com.xd.testrecorder.recorder.RecorderScreen
import com.xd.testrecorder.recording.ActionImageScreen
import com.xd.testrecorder.recording.ActionListScreen
import com.xd.testrecorder.recording.ProcessListScreen
import com.xd.testrecorder.recording.RecordingListScreen


@Composable
fun MainNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    logger: Logger = remember {
        Logger("Composable")
    },
    startDestination: String = MainDestinations.MAIN
) {
    CompositionLocalProvider(
        LocalNavController provides navController,
        LocalLogger provides logger
    ) {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = modifier
        ) {
            composable(
                MainDestinations.MAIN
            ) {
                MainScreen()
            }
            composable(MainDestinations.RECORD) {
                RecorderScreen()
            }
            composable(MainDestinations.PROCESS_LIST) {
                ProcessListScreen()
            }
            composable(MainDestinations.RECORDING_LIST) {
                RecordingListScreen()
            }
            composable(MainDestinations.RUN_PYTHON) {
                RunPythonScreen()
            }
            composable("${MainDestinations.ACTION_LIST}/{recordingId}") { backStackEntry ->
                val recordingId =
                    backStackEntry.arguments?.getString("recordingId")?.toLongOrNull() ?: 0L
                ActionListScreen(recordingId)
            }
            composable("${MainDestinations.ACTION_IMAGE}/{recordingId}/{actionId}") { backStackEntry ->
                val recordingId =
                    backStackEntry.arguments?.getString("recordingId")?.toLongOrNull() ?: 0L
                val actionId = backStackEntry.arguments?.getString("actionId")?.toLongOrNull() ?: 0L
                ActionImageScreen(recordingId, actionId)
            }
        }
    }
}
