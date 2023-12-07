package com.xd.testrecorder.codegen

import android.content.Context
import android.graphics.Bitmap
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.lifecycle.ViewModel
import com.xd.testrecorder.coroutine.ioMutable
import com.xd.testrecorder.data.Action
import com.xd.testrecorder.data.ActionCodeConverter
import com.xd.testrecorder.data.CodeConverterOptions
import com.xd.testrecorder.data.Recording
import com.xd.testrecorder.data.postNoneEqual
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
class CodeGeneratorViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    @Named("config")
    private val config: SharedPreferencesHelper
) : ViewModel() {
    private val logger = Logger("RecordingViewModel")
    private val optionKey = "code_gen_options"
    val options by lazy {
        ioMutable {
            config.getObject(
                optionKey,
                CodeConverterOptions::class.java,
                CodeConverterOptions()
            )!!
        }
    }

    init {
        logger.d("CodeGeneratorModel.init")
    }

    fun updateOptions(options: CodeConverterOptions) {
        config.putObject(optionKey, options, CodeConverterOptions::class.java)
        this.options.postNoneEqual(options)
    }

    fun getConverter(): ActionCodeConverter {
        return ActionCodeConverter.getConverter(options.value!!)
    }

    fun generateCode(recording: Recording, actions: List<Action>): CodeBlock {
        val converter = getConverter()
        var code = converter.getFun("${"unnamed"}() {")
        for (action in actions) {
            code += "\n\t${converter.toCode(action)}"
        }
        code += "\n\t//add you assertions"
        code += "\n}"
        return CodeBlock(code = code, funLines = 1)
    }

}

data class CodeBlock(val code: String = "", val funLines: Int = 0)

