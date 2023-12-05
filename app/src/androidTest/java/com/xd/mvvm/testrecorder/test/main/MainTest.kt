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

package com.xd.mvvm.testrecorder.test.main

import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.xd.mvvm.testrecorder.R
import com.xd.mvvm.testrecorder.test.utils.BaseTest
import dagger.hilt.android.testing.HiltAndroidTest
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

        composeTestRule.onNode(hasStringR(R.string.enter_weather_label)).performClick()

        // Click on the back button in the action bar
        composeTestRule.onNodeWithContentDescription(getStringR(R.string.navigate_up)).performClick()

        composeTestRule.onNode(hasStringR(R.string.enter_weather_label)).assertExists()
    }

    @Test
    fun testWeather2Navigation(){

        composeTestRule.onNode(hasStringR(R.string.enter_weather_cache_label)).performClick()

        // Click on the back button in the action bar
        composeTestRule.onNodeWithContentDescription(getStringR(R.string.navigate_up)).performClick()

        composeTestRule.onNode(hasStringR(R.string.enter_weather_cache_label)).assertExists()
    }

}
