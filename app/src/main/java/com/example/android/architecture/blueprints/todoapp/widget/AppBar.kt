package com.example.android.architecture.blueprints.todoapp.widget

import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

@Composable
fun AppBar(titleId: Int) {
    TopAppBar(
        title = { Text(text = stringResource(titleId)) }
    )
}