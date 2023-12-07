@file:OptIn(
    ExperimentalMaterialApi::class
)

package com.xd.common.widget

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.xd.common.data.D
import com.xd.common.data.Err
import com.xd.common.data.Loading
import com.xd.common.data.Success
import com.xd.common.logger.L
import com.xd.testrecorder.model.R

@Composable
fun <T> RefreshingLoadingContent(
    data: D<T>?,
    emptyContent: @Composable () -> Unit = { EmptyContent() },
    errorContent: (@Composable (message: String) -> Unit)? = null,
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
                WrapInLazyColumn {
                    if (errorContent != null) {
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
                    WrapInLazyColumn {
                        LoadingContent()
                    }
                }
            }

            null -> WrapInLazyColumn {
                emptyContent()
            }
        }

        PullRefreshIndicator(refreshing, state, Modifier.align(Alignment.TopCenter))
    }

}

@Composable
fun <T> DataLoadingContent(
    data: T?,
    loadingContent: (@Composable () -> Unit)? = null,
    emptyContent: (@Composable () -> Unit)? = null,
    content: @Composable (d: T) -> Unit
) {
    data?.let {
        if (it is Collection<*>) {
            // data is a Collection, you can get its size
            val size = it.size
            if (size == 0) {
                emptyContent?.let {
                    emptyContent()
                } ?: run {
                    EmptyContent()
                }
            } else {
                content(it)
            }
        }
    } ?: run {
        loadingContent?.let {
            loadingContent()
        } ?: run {
            LoadingContent()
        }
    }
}

// PullRefreshIndicator only works with a LazyColumn?
@Composable
private fun WrapInLazyColumn(content: @Composable () -> Unit) {
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
    modifier: Modifier = Modifier,
    @StringRes noTasksLabel: Int = R.string.no_data,
    @DrawableRes noTasksIconRes: Int = R.drawable.no_data
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = noTasksIconRes),
            contentDescription = stringResource(R.string.no_data),
            modifier = Modifier.size(96.dp)
        )
        Text(stringResource(id = noTasksLabel))
    }
}

@Composable
private fun LoadingContent(
    modifier: Modifier = Modifier,
    @DrawableRes noTasksIconRes: Int = R.drawable.loading
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
            contentDescription = stringResource(R.string.loading),
            modifier = Modifier.size(96.dp)
        )
        Text(stringResource(id = R.string.loading))
    }
}

@Composable
private fun ErrorContent(
    modifier: Modifier = Modifier,
    message: String,
    @DrawableRes noTasksIconRes: Int = R.drawable.request_error
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp)
            .testTag(stringResource(id = R.string.error)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = noTasksIconRes),
            contentDescription = stringResource(R.string.error),
            modifier = Modifier.size(96.dp)
        )
        Text(message)
    }
}