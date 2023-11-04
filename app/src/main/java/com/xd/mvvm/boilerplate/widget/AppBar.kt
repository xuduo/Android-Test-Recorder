package com.xd.mvvm.boilerplate.widget

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.xd.mvvm.boilerplate.LocalNavController

@Composable
fun AppBar(titleId: Int, showBackButton: Boolean = true) {
    val navController = LocalNavController.current
    TopAppBar(
        title = { Text(text = stringResource(titleId)) },
        navigationIcon = if (showBackButton) {
            {
                IconButton(onClick = {
                    navController.navigateUp()
                }) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        } else null
    )
}