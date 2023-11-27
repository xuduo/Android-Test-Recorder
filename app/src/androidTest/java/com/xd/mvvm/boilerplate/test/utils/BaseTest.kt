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

package com.xd.mvvm.boilerplate.test.utils

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.compose.ui.test.SemanticsMatcher
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
import com.xd.mvvm.boilerplate.MainActivity
import com.xd.mvvm.boilerplate.MainNavGraph
import com.xd.mvvm.boilerplate.R
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
open class BaseTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    // Executes tasks in the Architecture Components in the same thread
    @get:Rule(order = 1)
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule(order = 2)
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    private val activity get() = composeTestRule.activity

    @Before
    fun init() {
        hiltRule.inject()
    }

    protected fun setMainContent() {
        composeTestRule.setContent {
            AppCompatTheme {
                MainNavGraph()
            }
        }
    }


    protected fun hasStringR(r:Int): SemanticsMatcher {
        return hasText(getStringR(r))
    }

    protected fun findTextField(textId: Int): SemanticsNodeInteraction {
        return composeTestRule.onNode(
            hasSetTextAction() and hasText(getStringR(textId))
        )
    }

    protected fun getStringR(r: Int) = activity.getString(r)

    protected fun findTextField(text: String): SemanticsNodeInteraction {
        return composeTestRule.onNode(
            hasSetTextAction() and hasText(text)
        )
    }
}
