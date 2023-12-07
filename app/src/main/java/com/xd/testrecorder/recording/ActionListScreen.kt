package com.xd.testrecorder.recording

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.Button
import androidx.compose.material.RadioButton
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.xd.common.nav.LocalLogger
import com.xd.common.widget.AppBar
import com.xd.common.widget.DataLoadingContent
import com.xd.testrecorder.R
import com.xd.testrecorder.codegen.CodeGeneratorViewModel
import com.xd.testrecorder.data.CodeConverterOptions
import com.xd.testrecorder.data.Recording
import io.github.kbiakov.codeview.CodeView
import io.github.kbiakov.codeview.OnCodeLineClickListener
import io.github.kbiakov.codeview.adapters.Format
import io.github.kbiakov.codeview.adapters.Options
import io.github.kbiakov.codeview.highlight.ColorTheme

@Composable
fun ActionListScreen(
    recordingId: Long,
    modifier: Modifier = Modifier,
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {
    Scaffold(
        scaffoldState = scaffoldState,
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppBar(R.string.action_list) // Change the string resource to your app's name
        },
    ) {
        ActionListScreenContent(
            recordingId,
            modifier = Modifier.padding(it)
        )
    }
    @Composable
    fun ExampleComposable(nullableValue: String?) {
        nullableValue?.let { nonNullValue ->
            // This is a let block
            MyTextComposable(text = nonNullValue) // Calling a Composable function inside the let block
        }
    }


}

@Composable
fun MyTextComposable(text: String) {
    Text(text = text)
}

@Composable
private fun ActionListScreenContent(
    recordingId: Long,
    recordingModel: RecordingViewModel = hiltViewModel(),
    codeGenModel: CodeGeneratorViewModel = hiltViewModel(),
    modifier: Modifier,
) {
    val dataL by remember { recordingModel.getActionsByRecordingId(recordingId) }.observeAsState()
    val optionsL by codeGenModel.options.observeAsState()
    val recordingL by remember {
        recordingModel.getRecordingById(recordingId)
    }.observeAsState()
    val logger = LocalLogger.current
    LocalLogger.current.d("RecordingViewModel.getActionsByRecordingId() $dataL")
    val context = LocalContext.current
    com.xd.common.coroutine.let3(dataL, recordingL, optionsL) { data, recording, options ->
        val copy = {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(
                "label",
                codeGenModel.generateCode(recording, data).toString()
            )
            clipboard.setPrimaryClip(clip)
        }
        Column {
            SingleChoiceView(
                title = "Language",
                options = listOf("Java", "Kotlin"),
                options,
                copy = copy
            ) { selectedOption ->
                // Handle the selected option
                options.copy(lang = selectedOption).let { codeGenModel.updateOptions(it) }
            }
            val converter = codeGenModel.getConverter()
            DataLoadingContent(
                data
            ) {
                val code = codeGenModel.generateCode(Recording(), actions = it)
                AndroidView(factory = { ctx ->
                    // Create an Android View here. For example, a TextView.
                    CodeView(ctx).apply {
                        this.setOptions(
                            Options.get(ctx)
                                .withLanguage("kotlin")
                                .withFormat(
                                    Format(scaleFactor = 1.5f, fontSize = 18.dp.value)
                                )
                                .addCodeLineClickListener(object : OnCodeLineClickListener {
                                    override fun onCodeLineClicked(n: Int, line: String) {
                                        // Implement your logic here
                                        logger.i("code clicked ${n - code.funLines},$line")
                                    }
                                })
                                .withCode(code.code)
                                .withTheme(ColorTheme.SOLARIZED_LIGHT)
                        )
                    }
                },
                    update = { view ->
                        view.setCode(code = code.code)
                    })
            }
        }
    }
}

@Composable
fun SingleChoiceView(
    title: String,
    options: List<String>,
    codeOptions: CodeConverterOptions,
    copy: () -> Unit,
    onOptionSelected: (String) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = title, Modifier.padding(horizontal = 4.dp))
        Row {
            options.forEach { option ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .selectable(
                            selected = (option == codeOptions.lang),
                            onClick = {
                                onOptionSelected(option)
                            }
                        )
                        .padding(8.dp)
                ) {
                    RadioButton(
                        selected = (option == codeOptions.lang),
                        onClick = null // RadioButton's onClick is handled by Row
                    )
                    Text(text = option, modifier = Modifier.padding(start = 8.dp))
                }
            }
        }
        Box(modifier = Modifier.weight(1f))
        Button(onClick = copy) {
            Text("Copy")
        }
    }
}