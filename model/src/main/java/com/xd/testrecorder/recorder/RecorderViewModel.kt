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

package com.xd.testrecorder.recorder

import android.content.Context
import android.content.Intent
import android.graphics.SurfaceTexture
import android.media.projection.MediaProjectionManager
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.xd.common.coroutine.io
import com.xd.common.logger.Logger
import com.xd.testrecorder.dao.RecordingDao
import com.xd.testrecorder.data.Recording
import com.xd.testrecorder.recording.AppInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


@HiltViewModel
class RecorderViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val recordingDao: RecordingDao
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

    fun startCapture(launcher: ManagedActivityResultLauncher<Intent, ActivityResult>) {
        val mediaProjectionManager = ContextCompat.getSystemService(
            context, MediaProjectionManager::class.java
        ) as MediaProjectionManager

        val screenCaptureIntent = mediaProjectionManager.createScreenCaptureIntent()
        launcher.launch(screenCaptureIntent)
    }

    fun startRecording(appInfo: AppInfo) {
        val recording = Recording().apply {
            this.name = appInfo.appName
            this.packageName = appInfo.packageName
        }
        io {
            recordingDao.insertRecording(recording)
        }
    }

    fun getAllRecordings() {
        val recordings = recordingDao.getAllRecordings()

    }

    fun handleScreenCaptureResult(resultCode: Int, data: Intent?) {
        val serviceIntent = Intent(context, RecorderService::class.java)
        RecorderService.startMediaIntent = data
        RecorderService.resultCode = resultCode
        ContextCompat.startForegroundService(context, serviceIntent)
    }

}

