package com.xd.testrecorder.scripts

import androidx.lifecycle.ViewModel
import com.xd.common.logger.Logger
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class ScriptingViewModel @Inject constructor(
) : ViewModel() {
    private val logger = Logger("ScriptingViewModel")

    init {
        logger.d("RecordingViewModel.init")
        System.loadLibrary("opencv_java4")
    }
}
