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

package com.xd.mvvm.boilerplate.test.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.google.accompanist.themeadapter.appcompat.AppCompatTheme
import com.xd.mvvm.boilerplate.HiltTestActivity
import com.xd.mvvm.boilerplate.MainNavGraph
import com.xd.mvvm.boilerplate.R
import com.xd.mvvm.boilerplate.test.utils.BaseTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Large End-to-End test for the tasks module.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
@HiltAndroidTest
class MainTest : BaseTest() {

    @Test
    fun testWeather1Navigation(){
        setMainContent()

        composeTestRule.onNode(hasStringR(R.string.enter_weather_label)).performClick()

        // Click on the back button in the action bar
        composeTestRule.onNodeWithContentDescription(getStringR(R.string.navigate_up)).performClick()

        composeTestRule.onNode(hasStringR(R.string.enter_weather_label)).assertExists()
    }

    @Test
    fun testWeather2Navigation(){
        setMainContent()

        composeTestRule.onNode(hasStringR(R.string.enter_weather_cache_label)).performClick()

        // Click on the back button in the action bar
        composeTestRule.onNodeWithContentDescription(getStringR(R.string.navigate_up)).performClick()

        composeTestRule.onNode(hasStringR(R.string.enter_weather_cache_label)).assertExists()
    }

//    @Test
//    fun editTask() = runTest {
//        val originalTaskTitle = "Main"
//
//        setContent()
//
//        // Click on the task on the list and verify that all the data is correct
//        composeTestRule.onNodeWithText(activity.getString(R.string.label_all)).assertIsDisplayed()
//        composeTestRule.onNodeWithText(originalTaskTitle).assertIsDisplayed()
//        composeTestRule.onNodeWithText(originalTaskTitle).performClick()
//
//        // Task detail screen
//        composeTestRule.onNodeWithText(activity.getString(R.string.task_details))
//            .assertIsDisplayed()
//        composeTestRule.onNodeWithText(originalTaskTitle).assertIsDisplayed()
//        composeTestRule.onNodeWithText("DESCRIPTION").assertIsDisplayed()
//        composeTestRule.onNode(isToggleable()).assertIsOff()
//
//        // Click on the edit button, edit, and save
//        composeTestRule.onNodeWithContentDescription(activity.getString(R.string.edit_task))
//            .performClick()
//        composeTestRule.onNodeWithText(activity.getString(R.string.edit_task)).assertIsDisplayed()
//        findTextField(originalTaskTitle).performTextReplacement("NEW TITLE")
//        findTextField("DESCRIPTION").performTextReplacement("NEW DESCRIPTION")
//        composeTestRule.onNodeWithContentDescription(activity.getString(R.string.cd_save_task))
//            .performClick()
//
//        // Verify task is displayed on screen in the task list.
//        composeTestRule.onNodeWithText(activity.getString(R.string.label_all)).assertIsDisplayed()
//        composeTestRule.onNodeWithText("NEW TITLE").assertIsDisplayed()
//        // Verify previous task is not displayed
//        composeTestRule.onNodeWithText(originalTaskTitle).assertDoesNotExist()
//    }
//
//    @Test
//    fun createOneTask_deleteTask() {
//        setContent()
//
//        val taskTitle = "TITLE1"
//        // Add active task
//        composeTestRule.onNodeWithContentDescription(activity.getString(R.string.add_task))
//            .performClick()
//        findTextField(R.string.title_hint).performTextInput(taskTitle)
//        findTextField(R.string.description_hint).performTextInput("DESCRIPTION")
//        composeTestRule.onNodeWithContentDescription(activity.getString(R.string.cd_save_task))
//            .performClick()
//        composeTestRule.onNodeWithText(activity.getString(R.string.label_all)).assertIsDisplayed()
//        composeTestRule.onNodeWithText(taskTitle).assertIsDisplayed()
//
//        // Open the task detail screen
//        composeTestRule.onNodeWithText(taskTitle).performClick()
//        composeTestRule.onNodeWithText(activity.getString(R.string.task_details))
//            .assertIsDisplayed()
//        // Click delete task in menu
//        composeTestRule.onNodeWithContentDescription(activity.getString(R.string.menu_delete_task))
//            .performClick()
//
//        // Verify it was deleted
//        composeTestRule.onNodeWithContentDescription(activity.getString(R.string.menu_filter))
//            .performClick()
//        composeTestRule.onNodeWithText(activity.getString(R.string.nav_all)).assertIsDisplayed()
//        composeTestRule.onNodeWithText(taskTitle).assertDoesNotExist()
//    }

}
