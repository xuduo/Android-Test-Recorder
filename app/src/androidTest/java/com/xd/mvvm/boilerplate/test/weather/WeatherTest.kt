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

package com.xd.mvvm.boilerplate.test.weather

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
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
@OptIn(ExperimentalTestApi::class)
@RunWith(AndroidJUnit4::class)
@LargeTest
@HiltAndroidTest
class WeatherTest : BaseTest() {

    @Test
    fun testWeather(){
        composeTestRule.onNode(hasStringR(R.string.enter_weather_label)).performClick()
        composeTestRule.onNode(hasStringR(R.string.weather)).assertExists()
        composeTestRule.onNode(hasStringR(R.string.loading)).assertExists()
        composeTestRule.waitUntilAtLeastOneExists(hasText("°C", substring = true, ignoreCase = true),5000)
        composeTestRule.onNodeWithContentDescription(getStringR(R.string.navigate_up)).performClick()
        // Click on the back button in the action bar
        composeTestRule.onNodeWithTag(getStringR(R.string.simulate_http_error)).assertIsOff().performClick()

        composeTestRule.onNode(hasStringR(R.string.enter_weather_label)).performClick()
        composeTestRule.onNode(hasStringR(R.string.weather)).assertExists()
        composeTestRule.onNode(hasStringR(R.string.loading)).assertExists()
        composeTestRule.waitUntilAtLeastOneExists(hasTestTag(getStringR(R.string.error)),10000)
    }

}
