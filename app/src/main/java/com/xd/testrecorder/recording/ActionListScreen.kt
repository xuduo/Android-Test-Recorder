package com.xd.testrecorder.recording

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import com.xd.common.coroutine.let3
import com.xd.common.nav.LocalLogger
import com.xd.common.nav.LocalNavController
import com.xd.common.widget.AppBar
import com.xd.common.widget.LoadingContent
import com.xd.testrecorder.R
import com.xd.testrecorder.codegen.CodeGeneratorViewModel
import com.xd.testrecorder.data.CodeConverterOptions
import com.xd.testrecorder.data.Recording
import com.xd.testrecorder.goToActionImage
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

    let3(dataL, recordingL, optionsL) { actions, recording, options ->
        val copy = {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val code = codeGenModel.generateCode(recording, actions).code
            val clip = ClipData.newPlainText(
                "code",
                code
            )
            clipboard.setPrimaryClip(clip)
            Toast
                .makeText(context, "${code.lines().size} lines of code copied", Toast.LENGTH_SHORT)
                .show()
        }
        Column {
            SingleChoiceView(
                title = "Language:",
                options = listOf("Java", "Kotlin"),
                options,
                copy = copy
            ) { selectedOption ->
                // Handle the selected option
                options.copy(lang = selectedOption).let { codeGenModel.updateOptions(it) }
            }

            val code = codeGenModel.generateCode(Recording(), actions = actions)
            val nav = LocalNavController.current
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
                                    val index = n - code.funLines
                                    logger.i("code clicked ${index},$line")
                                    if (index in actions.indices) {
                                        nav.goToActionImage(
                                            recordingId = recordingId,
                                            actions[index].id
                                        )
                                    }
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
    } ?: run {
        LoadingContent()
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
        Text(text = title, Modifier.padding(start = 8.dp))
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