package com.xd.testrecorder.codegen

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.xd.testrecorder.coroutine.ioLiveData
import com.xd.testrecorder.data.CodeConverterOptions
import com.xd.testrecorder.logger.Logger
import com.xd.testrecorder.sharedpref.SharedPreferencesHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Named


data class AppInfo(
    val icon: Bitmap,
    val packageName: String,
    val appName: String,
    val lastTimeUsed: Long
)

@HiltViewModel
class CodeGeneratorModel @Inject constructor(
    @ApplicationContext private val context: Context,
    @Named("config")
    private val config: SharedPreferencesHelper
) : ViewModel() {
    private val logger = Logger("RecordingViewModel")
    private val optionKey = "code_gen_options"
    val options by lazy {
        ioLiveData<CodeConverterOptions> {
            config.getObject(
                optionKey,
                CodeConverterOptions::class.java,
                CodeConverterOptions()
            )
        }
    }

    init {
        logger.d("CodeGeneratorModel.init")
    }

    fun updateOptions(options: CodeConverterOptions) {
    }


}

