package com.xd.mvvm.testrecorder.recording

import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.xd.mvvm.testrecorder.LocalLogger
import com.xd.mvvm.testrecorder.R
import com.xd.mvvm.testrecorder.widget.AppBar

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
    val logger = LocalLogger.current
    val actions by viewModel.getActionsByRecordingId(recordingId)
        .observeAsState()
    actions?.let {
        val pagerState =
            rememberPagerState(initialPage = it.indexOfFirst { item -> item.id == actionId }) { it.size }
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) { page ->
            val action = actions?.get(page)
            action?.let { theAction ->
                LaunchedEffect(pagerState.currentPage) {
                    // This block is called when the currentPage changes
                    updateTitle("Action ${pagerState.currentPage + 1}/${it.size} ${action.type} on ${action.viewClassName}, ${if (action.viewContentDescription == "") "no contentDescription" else "contentDescription" + action.viewContentDescription} ")
                }
                val actionImage by remember(theAction.id) {
                    viewModel.getActionImage(theAction.id)
                }.observeAsState()
                Log.d(
                    "ActionImageScreen",
                    "draw action page cords:${theAction.cords} bounds:${theAction.viewBounds}  screen:${theAction.screenWidth}x${theAction.screenHeight} ${actionImage?.screenShot?.size}"
                )
                actionImage?.let { theActionImage ->
                    val imageBitmap =
                        BitmapFactory.decodeByteArray(
                            theActionImage.screenShot,
                            0,
                            theActionImage.screenShot.size
                        )
                            .asImageBitmap()
                    BoxWithConstraints(
                        modifier
                            .fillMaxSize()) {
                        // Calculate aspect ratio
                        val aspectRatioBox = maxWidth / maxHeight
                        val aspectRatioImage =
                            imageBitmap.width.toFloat() / imageBitmap.height.toFloat()
                        val ratio = aspectRatioBox / aspectRatioImage
                        val mod = if (ratio < 1) {
                            // Image is wider than box, add vertical padding
                            val scaledImageHeight = maxHeight.value * ratio
                            val totalPadding = maxHeight.value - scaledImageHeight
                            val padding = totalPadding / 2
                            Log.d(
                                "ActionImageScreen",
                                "padding v $ratio $padding $aspectRatioBox $aspectRatioImage ${imageBitmap.width}x${imageBitmap.height} $maxWidth"
                            )
                            modifier
                                .padding(top = padding.dp, bottom = padding.dp)
                        } else {
                            // Image is taller than box, add horizontal padding
                            val scaledImageWidth = maxWidth.value / ratio
                            val totalPadding = maxWidth.value - scaledImageWidth
                            val padding = totalPadding / 2
                            Log.d(
                                "ActionImageScreen",
                                "padding h $ratio $padding $aspectRatioBox $aspectRatioImage ${imageBitmap.width}x${imageBitmap.height} $maxWidth"
                            )
                            modifier
                                .padding(start = padding.dp, end = padding.dp)
                        }
                        BoxWithConstraints(modifier = mod) {
                            Image(
                                bitmap = imageBitmap,
                                modifier = modifier
                                    .padding(0.dp)
                                    .fillMaxSize(),
                                contentDescription = "Screenshot", // Provide a description for accessibility
                                contentScale = ContentScale.FillBounds // Adjust content scale as needed
                            )
//                            Box(modifier = modifier
//                                .background(Color.Yellow)
//                                .fillMaxSize())
                            Image(
                                painter = painterResource(id = R.drawable.touch_app),
                                contentDescription = null, // Provide a suitable content description
                                modifier = Modifier.offset(
                                    x = (action.getRatioXOnScreen() * maxWidth.value).dp,
                                    y = (action.getRatioYOnScreen() * maxHeight.value).dp
                                )
                            )
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                // Draw the outer rectangle (the border)
                                val rect = action.getRelativeViewBounds(
                                    size
                                )
                                logger.d("rect ${action.viewBounds} $rect")
                                drawRect(
                                    color = Color.Red,
                                    size = Size(
                                        width = rect.width().toFloat(), height = rect.height()
                                            .toFloat()
                                    ),
                                    topLeft = Offset(
                                        x = rect.left.toFloat(),
                                        y = rect.top.toFloat()
                                    ),
                                    style = Stroke(width = 1.dp.toPx())
                                )
                            }
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
