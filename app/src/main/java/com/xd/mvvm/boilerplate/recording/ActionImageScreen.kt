package com.xd.mvvm.boilerplate.recording

import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.xd.mvvm.boilerplate.R
import com.xd.mvvm.boilerplate.widget.AppBar

@Composable
fun ActionImageScreen(
    recordingId: Long,
    actionId: Long,
    modifier: Modifier = Modifier,
    scaffoldState: ScaffoldState = rememberScaffoldState()
) {
    var topBarTitle by remember { mutableStateOf("Action") }
    Scaffold(
        scaffoldState = scaffoldState,
        modifier = modifier.fillMaxSize(),
        topBar = {
            AppBar(titleText = topBarTitle) // Change the string resource to your app's name
        },
    ) {
        ActionImageScreenContent(
            recordingId,
            actionId,
            updateTitle = { newTitle -> topBarTitle = newTitle },
            modifier = Modifier.padding(it)
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ActionImageScreenContent(
    recordingId: Long,
    actionId: Long,
    updateTitle: (String) -> Unit,
    viewModel: RecordingViewModel = hiltViewModel(),
    modifier: Modifier,
) {
    val actions by viewModel.getActionsByRecordingId(recordingId)
        .observeAsState()
    actions?.value?.let {
        val pagerState =
            rememberPagerState(initialPage = it.indexOfFirst { item -> item.id == actionId }) { it.size }
        LaunchedEffect(pagerState.currentPage) {
            // This block is called when the currentPage changes
            updateTitle("Action ${pagerState.currentPage + 1}/${it.size}")
        }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) { page ->
            val action = actions?.value?.get(page)
            action?.let { theAction ->
                val actionImage by remember(theAction.id) {
                    viewModel.getActionImage(theAction.id)
                }.observeAsState()
                Log.d(
                    "ActionImageScreen",
                    "draw page ${actionImage?.id} ${theAction.id} ${theAction.cords}  ${actionImage?.screenShot?.size}"
                )
                actionImage?.let {
                    val imageBitmap =
                        BitmapFactory.decodeByteArray(it.screenShot, 0, it.screenShot.size)
                            .asImageBitmap()
                    BoxWithConstraints {
                        // Calculate aspect ratio
                        val aspectRatioBox = maxWidth / maxHeight
                        val aspectRatioImage =
                            imageBitmap.width.toFloat() / imageBitmap.height.toFloat()
                        val mod = if (aspectRatioImage > aspectRatioBox) {
                            // Image is wider than box, add vertical padding
                            val scaledImageHeight = maxWidth.value / aspectRatioImage
                            val totalPadding = maxHeight.value - scaledImageHeight
                            val padding = totalPadding / 2
                            Log.d("ActionImageScreen", "padding v $padding")
                            modifier
                                .padding(top = padding.dp, bottom = padding.dp)
                        } else {
                            // Image is taller than box, add horizontal padding
                            val scaledImageWidth = maxHeight.value * aspectRatioImage
                            val totalPadding = maxWidth.value - scaledImageWidth
                            val padding = totalPadding / 2
                            Log.d("ActionImageScreen", "padding h $padding")
                            modifier
                                .padding(start = padding.dp, end = padding.dp)
                        }
                        BoxWithConstraints(modifier = mod) {
                            Image(
                                bitmap = imageBitmap,
                                contentDescription = "Screenshot", // Provide a description for accessibility
                                contentScale = ContentScale.Crop // Adjust content scale as needed
                            )
                            Image(
                                painter = painterResource(id = R.drawable.touch_app),
                                contentDescription = null, // Provide a suitable content description
                                modifier = Modifier.offset(x = (action.getRatioXOnScreen() * maxWidth.value).dp, y = (action.getRatioYOnScreen() * maxHeight.value).dp)
                            )
                        }
                    }
                }
            }
        }

    }
}

fun translateRealToUI(
    imageWidth: Int,
    imageHeight: Int, x: Int, // Relative 0 to imageWidth
    y: Int,
) {

}

@Composable
fun ClickImage(
    imageWidth: Int,
    imageHeight: Int,
    x: Int, // Relative 0 to imageWidth
    y: Int, // Relative 0 to imageHeight
    modifier: Modifier = Modifier
) {

}
