@file:OptIn(
    ExperimentalMaterialApi::class
)

package com.xd.mvvm.boilerplate.util

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xd.mvvm.boilerplate.R
import com.xd.mvvm.boilerplate.data.D
import com.xd.mvvm.boilerplate.data.Err
import com.xd.mvvm.boilerplate.data.Loading
import com.xd.mvvm.boilerplate.data.Success
import com.xd.mvvm.boilerplate.logger.L

@Composable
fun <T> DataLoadingContent(
    data: D<T>?,
    emptyContent: @Composable () -> Unit = { EmptyContent() },
    errorContent: (@Composable (message:String) -> Unit)? = null,
    onRefresh: () -> Unit,
    content: @Composable (d: T) -> Unit
) {
    var refreshing = data is Loading

    val state = rememberPullRefreshState(refreshing, onRefresh)

    Box(Modifier.pullRefresh(state)) {
        when (data) {
            is Success -> {
                L.d("DataLoadingContent D.Success")
                content(data.value)
            }

            is Err -> {
                L.d("DataLoadingContent D.Error")
                wrapInLazyColumn {
                    if(errorContent != null){
                        errorContent(data.errorMessage)
                    } else {
                        ErrorContent(message = data.errorMessage)
                    }
                }
            }

            is Loading -> {
                refreshing = true
                L.d("DataLoadingContent D.Loading")
                data.value?.let {
                    content(it)
                } ?: run {
                    wrapInLazyColumn {
                        emptyContent()
                    }
                }
            }

            null -> wrapInLazyColumn {
                emptyContent()
            }
        }

        PullRefreshIndicator(refreshing, state, Modifier.align(Alignment.TopCenter))
    }

}

// PullRefreshIndicator only works with a LazyColumn?
@Composable
private fun wrapInLazyColumn(content: @Composable () -> Unit) {
    LazyColumn(Modifier.fillMaxSize()) {
        items(1) {
            Box(Modifier.fillParentMaxSize()) {
                content()
            }
        }
    }
}

@Preview
@Composable
private fun EmptyContent(
    @StringRes noTasksLabel: Int = R.string.no_data,
    @DrawableRes noTasksIconRes: Int = R.drawable.no_data,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = noTasksIconRes),
            contentDescription = stringResource(R.string.no_tasks_image_content_description),
            modifier = Modifier.size(96.dp)
        )
        Text(stringResource(id = noTasksLabel))
    }
}

@Composable
private fun ErrorContent(
    message: String,
    @DrawableRes noTasksIconRes: Int = R.drawable.request_error,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = noTasksIconRes),
            contentDescription = stringResource(R.string.no_tasks_image_content_description),
            modifier = Modifier.size(96.dp)
        )
        Text(message)
    }
}