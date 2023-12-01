/*
 * Copyright 2019 The Android Open Source Project
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

package com.xd.mvvm.boilerplate.recorder

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.SurfaceTexture
import android.hardware.display.DisplayManager
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.view.Surface
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.xd.mvvm.boilerplate.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


@HiltViewModel
class RecorderViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val logger = Logger("RecorderViewModel")
    private val surfaceTexture = SurfaceTexture(1)

    init {
        logger.d("RecorderViewModel.init")
        surfaceTexture.setOnFrameAvailableListener {
            // Process frame here
            logger.d("setOnFrameAvailableListener")
        }
    }

    fun startRecording(launcher: ManagedActivityResultLauncher<Intent, ActivityResult>) {
        val mediaProjectionManager = ContextCompat.getSystemService(
            context, MediaProjectionManager::class.java
        ) as MediaProjectionManager

        val screenCaptureIntent = mediaProjectionManager.createScreenCaptureIntent()
        launcher.launch(screenCaptureIntent)
    }

    fun handleScreenCaptureResult(resultCode: Int, data: Intent?) {
        val serviceIntent = Intent(context, RecorderService::class.java)
        RecorderService.startMediaIntent = data
        RecorderService.resultCode = resultCode
        ContextCompat.startForegroundService(context, serviceIntent)
    }


}

