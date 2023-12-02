package com.xd.mvvm.boilerplate.overlay

import android.accessibilityservice.AccessibilityService.GestureResultCallback
import android.accessibilityservice.GestureDescription
import android.app.Service
import android.content.Intent
import android.gesture.GestureOverlayView
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.ImageFormat
import android.graphics.PixelFormat
import android.media.Image
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import com.xd.mvvm.boilerplate.accessibility.TouchAccessibilityService
import com.xd.mvvm.boilerplate.coroutine.io
import com.xd.mvvm.boilerplate.dao.ActionDao
import com.xd.mvvm.boilerplate.dao.ActionImageDao
import com.xd.mvvm.boilerplate.data.Action
import com.xd.mvvm.boilerplate.data.ActionImage
import com.xd.mvvm.boilerplate.data.convertImageToByteArray
import com.xd.mvvm.boilerplate.data.convertMotionEventsToAction
import com.xd.mvvm.boilerplate.logger.Logger
import com.xd.mvvm.boilerplate.recorder.RecorderService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import javax.inject.Inject

@AndroidEntryPoint
class OverlayService : Service() {

    companion object {
        var service: OverlayService? = null
    }

    var recordingId = -1L

    @Inject
    lateinit var actionImageDao: ActionImageDao
    lateinit var actionDao: ActionDao

    private lateinit var windowManager: WindowManager
    private lateinit var overlayView: View
    private val type: Int = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
        WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
    } else {
        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
    }
    private val flagsPassThrough = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
    private val flagsCapture = WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
    private val params = WindowManager.LayoutParams(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.MATCH_PARENT,
        type,
        flagsCapture,
        PixelFormat.TRANSLUCENT
    )

    private val motionEventList = mutableListOf<MotionEvent>()
    private val gestureCallback = object : GestureResultCallback() {

        override fun onCancelled(gestureDescription: GestureDescription?) {
            changeToCapture()
            logger.d("gestureCallback onCancelled")
        }

        override fun onCompleted(gestureDescription: GestureDescription?) {
            changeToCapture()
            logger.d("gestureCallback onCompleted")
        }
    }

    private val logger = Logger("OverlayService")

    override fun onBind(intent: Intent?): IBinder? {
        logger.d("onBind")
        service = this
        return null
    }

    override fun onCreate() {
        super.onCreate()
        service = this
        removeOverlay()
        logger.d("onCreate")
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        overlayView = GestureOverlayView(this)
        overlayView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        overlayView.setBackgroundColor(Color.parseColor("#20FF0000"))

// Set OnTouchListener
        overlayView.setOnTouchListener { _, event ->
            // Handle touch events here
            logger.d("OnTouchListener $event ${isPassThrough()}")
            if (isPassThrough()) {
                true
            }
            motionEventList.add(copyMotionEvent(event))
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                }

                MotionEvent.ACTION_MOVE -> {
                    // Do something when the finger moves on the screen
                }

                MotionEvent.ACTION_UP -> {
                    handleMotionActionUp()
                }
            }
            false // Return true if the listener has consumed the event, false otherwise.
        }

// Update your WindowManager LayoutParams

        params.gravity = Gravity.TOP or Gravity.START
        params.x = 0;
        params.y = 0;

        if (TouchAccessibilityService.isTargetPackage()) {
            params.flags = flagsCapture
        } else {
            params.flags = flagsPassThrough
        }

        overlayView.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                // The overlay view is attached to the window
            }

            override fun onViewDetachedFromWindow(v: View) {
                // The overlay view is detached from the window
                removeOverlay()
            }
        })
        windowManager.addView(overlayView, params)
        logger.d("onCreate done")
    }

    private fun handleMotionActionUp() {
        changeToPassThrough()
        if (recordingId < 0) {
            logger.e("recordingId less than 0")
            return
        }
        val action = convertMotionEventsToAction(motionEventList, recordingId)
        motionEventList.clear()
        val gesture = action?.toGestureDescription()
        val image = RecorderService.service?.latestImage
        if (action == null || image == null || image.format != ImageFormat.YUV_420_888) {
            logger.w("changeToCapture $action $gesture $image")
            changeToCapture()
        } else {
            io {
                val actionId = actionDao.insertAction(action)
                val actionImage = ActionImage(actionId = actionId, screenShot = convertImageToByteArray(image, TouchAccessibilityService.getStatusBarHeight()))
                actionImageDao.insertActionImage(actionImage)
                withContext(Dispatchers.Main){
                    TouchAccessibilityService.dispatchGesture(
                        gesture,
                        gestureCallback
                    )
                }
            }
        }
    }

    fun adjustPassThrough() {
        if (TouchAccessibilityService.isTargetPackage()) {
            changeToCapture()
        } else {
            changeToPassThrough()
        }
    }

    private fun copyMotionEvent(originalEvent: MotionEvent): MotionEvent {
        return MotionEvent.obtain(
            originalEvent.downTime,
            originalEvent.eventTime,
            originalEvent.action,
            originalEvent.x,
            originalEvent.y,
            originalEvent.metaState
        )
    }

    private fun isPassThrough(): Boolean {
        return params.flags == flagsPassThrough
    }

    private fun changeToPassThrough() {
        params.flags = flagsPassThrough
        windowManager.updateViewLayout(overlayView, params)
    }

    private fun changeToCapture() {
        motionEventList.clear()
        params.flags = flagsCapture
        windowManager.updateViewLayout(overlayView, params)
    }

    override fun onDestroy() {
        super.onDestroy()
        logger.d("onDestroy")
        service = null
        removeOverlay()
    }

    private fun removeOverlay() {
        if (::overlayView.isInitialized) {
            windowManager.removeView(overlayView)
        }
    }
}