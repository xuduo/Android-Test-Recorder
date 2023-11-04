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

package com.xd.mvvm.boilerplate

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.xd.mvvm.boilerplate.TodoDestinationsArgs.TASK_ID_ARG
import com.xd.mvvm.boilerplate.TodoDestinationsArgs.TITLE_ARG
import com.xd.mvvm.boilerplate.TodoDestinationsArgs.USER_MESSAGE_ARG
import com.xd.mvvm.boilerplate.addedittask.AddEditTaskScreen
import com.xd.mvvm.boilerplate.statistics.StatisticsScreen
import com.xd.mvvm.boilerplate.taskdetail.TaskDetailScreen
import com.xd.mvvm.boilerplate.tasks.MainScreen
import com.xd.mvvm.boilerplate.weather.WeatherScreen

val LocalNavController = compositionLocalOf<NavController> {
    error("No NavController provided")
}

@Composable
fun MainNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = MainDestinations.TASKS_ROUTE,
    navActions: TodoNavigationActions = remember(navController) {
        TodoNavigationActions(navController)
    }
) {
    CompositionLocalProvider(LocalNavController provides navController) {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = modifier
        ) {
            composable(
                MainDestinations.TASKS_ROUTE,
                arguments = listOf(
                    navArgument(USER_MESSAGE_ARG) { type = NavType.IntType; defaultValue = 0 }
                )
            ) { entry ->
                MainScreen(
                    navActions = navActions,
                )
            }
            composable(MainDestinations.WEATHER) {
                WeatherScreen()
            }
            composable(MainDestinations.STATISTICS_ROUTE) {
                StatisticsScreen()
            }
            composable(
                MainDestinations.ADD_EDIT_TASK_ROUTE,
                arguments = listOf(
                    navArgument(TITLE_ARG) { type = NavType.IntType },
                    navArgument(TASK_ID_ARG) { type = NavType.StringType; nullable = true },
                )
            ) { entry ->
                val taskId = entry.arguments?.getString(TASK_ID_ARG)
                AddEditTaskScreen(
                    topBarTitle = entry.arguments?.getInt(TITLE_ARG)!!,
                    onTaskUpdate = {
                        navActions.navigateToTasks(
                            if (taskId == null) ADD_EDIT_RESULT_OK else EDIT_RESULT_OK
                        )
                    },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(MainDestinations.TASK_DETAIL_ROUTE) {
                TaskDetailScreen(
                    onEditTask = { taskId ->
                        navActions.navigateToAddEditTask(R.string.edit_task, taskId)
                    },
                    onBack = { navController.popBackStack() },
                    onDeleteTask = { navActions.navigateToTasks(DELETE_RESULT_OK) }
                )
            }
        }
    }
}

// Keys for navigation
const val ADD_EDIT_RESULT_OK = Activity.RESULT_FIRST_USER + 1
const val DELETE_RESULT_OK = Activity.RESULT_FIRST_USER + 2
const val EDIT_RESULT_OK = Activity.RESULT_FIRST_USER + 3
