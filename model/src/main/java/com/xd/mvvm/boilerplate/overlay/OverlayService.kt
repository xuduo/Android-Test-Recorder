package com.xd.mvvm.boilerplate.overlay

import android.accessibilityservice.AccessibilityService.GestureResultCallback
import android.accessibilityservice.GestureDescription
import android.app.Service
import android.content.Intent
import android.gesture.GestureOverlayView
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import com.xd.mvvm.boilerplate.accessibility.TouchAccessibilityService
import com.xd.mvvm.boilerplate.logger.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class OverlayService : Service() {
    companion object{
        var service: OverlayService? = null
    }
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
    private val params =  WindowManager.LayoutParams(
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
//        overlayView.setBackgroundColor(Color.parseColor("#FFFF0000"))
//        overlayView.setBackgroundColor(Color.parseColor("#00000000"))

// Set OnTouchListener
        overlayView.setOnTouchListener { _, event ->
            // Handle touch events here
            logger.d("OnTouchListener $event")
            motionEventList.add(copyMotionEvent(event))
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                }
                MotionEvent.ACTION_MOVE -> {
                    // Do something when the finger moves on the screen
                }
                MotionEvent.ACTION_UP -> {
                        changeToPassThrough()
                        CoroutineScope(Dispatchers.Main).launch {
                            // Perform UI operations here
                            delay(1)
                            TouchAccessibilityService.dispatchGesture(motionEventList, gestureCallback)
                        }
                }
            }
            false // Return true if the listener has consumed the event, false otherwise.
        }

// Update your WindowManager LayoutParams

        params.gravity = Gravity.TOP or Gravity.START
        params.x = 0;
        params.y = 0;

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

    private fun changeToPassThrough(){
        params.flags = flagsPassThrough
        windowManager.updateViewLayout(overlayView, params)
    }

    private fun changeToCapture(){
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