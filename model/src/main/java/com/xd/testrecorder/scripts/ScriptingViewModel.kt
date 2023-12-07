package com.xd.testrecorder.scripts

import android.content.Context
import androidx.lifecycle.ViewModel
import com.xd.testrecorder.dao.ActionDao
import com.xd.testrecorder.dao.ActionImageDao
import com.xd.testrecorder.dao.RecordingDao
import com.xd.testrecorder.logger.Logger
import com.xd.testrecorder.model.BuildConfig
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
