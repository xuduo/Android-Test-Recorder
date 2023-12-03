package com.xd.mvvm.boilerplate.scripts

import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.graphics.Bitmap
import android.media.projection.MediaProjectionManager
import android.provider.Settings
import android.text.TextUtils
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.xd.mvvm.boilerplate.accessibility.TouchAccessibilityService
import com.xd.mvvm.boilerplate.coroutine.io
import com.xd.mvvm.boilerplate.dao.ActionDao
import com.xd.mvvm.boilerplate.dao.ActionImageDao
import com.xd.mvvm.boilerplate.dao.RecordingDao
import com.xd.mvvm.boilerplate.data.Action
import com.xd.mvvm.boilerplate.data.ActionImage
import com.xd.mvvm.boilerplate.data.D
import com.xd.mvvm.boilerplate.data.Recording
import com.xd.mvvm.boilerplate.data.asD
import com.xd.mvvm.boilerplate.logger.Logger
import com.xd.mvvm.boilerplate.model.BuildConfig
import com.xd.mvvm.boilerplate.overlay.OverlayService
import com.xd.mvvm.boilerplate.recorder.RecorderService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
//import org.opencv.android.OpenCVLoader
//import org.opencv.core.MatOfByte
//import org.opencv.imgcodecs.Imgcodecs
import javax.inject.Inject


@HiltViewModel
class ScriptingViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val recordingDao: RecordingDao,
    private val actionDao: ActionDao,
    private val actionImageDao: ActionImageDao,
) : ViewModel() {
    private val logger = Logger("ScriptingViewModel")

    init {
        logger.d("RecordingViewModel.init")
        if (BuildConfig.DEBUG) {
//            OpenCVLoader.initDebug()
        } else {
            System.loadLibrary("opencv_java4")
        }
    }

    fun findFeatures(imageBytes: ByteArray){
//        val matOfByte = MatOfByte(*imageBytes)
//
//        // Decode the MatOfByte into a Mat
//        val mat = Imgcodecs.imdecode(matOfByte, Imgcodecs.IMREAD_UNCHANGED)
    }
}
