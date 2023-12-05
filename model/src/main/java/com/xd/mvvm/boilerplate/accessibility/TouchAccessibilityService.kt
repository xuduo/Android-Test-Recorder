package com.xd.mvvm.boilerplate.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.view.MotionEvent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.core.graphics.toRect
import androidx.core.graphics.toRectF
import com.xd.mvvm.boilerplate.logger.Logger
import com.xd.mvvm.boilerplate.overlay.OverlayService
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TouchAccessibilityService : AccessibilityService() {

    private val logger = Logger("TouchAccessibilityService")
    private var statusBarHeight = 0
    var recordingPackageName = "not.recording"
    private var foregroundPackageName = "no"

    companion object {
        var service: TouchAccessibilityService? = null

        fun isTargetPackage(): Boolean {
            return service?.recordingPackageName == service?.foregroundPackageName
        }

        fun getStatusBarHeight(): Int {
            return service?.getStatusBarHeight() ?: 0
        }

        fun dispatchGesture(
            gesture: GestureDescription?,
            callback: GestureResultCallback
        ): Boolean {
            val service = service ?: return false
            // Check if 'gesture' is not null
            if (gesture != null) {
                // Dispatch the gesture and return true
                service.dispatchGesture(gesture, callback, null)
                return true
            }

            // Return false if 'gesture' is null
            return false
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        logger.d("onServiceConnected")
        service = this
    }

    override fun onCreate() {
        super.onCreate()
        service = this
        statusBarHeight = getStatusBarHeight()
        logger.d("statusBarHeight $statusBarHeight")
    }

    override fun onDestroy() {
        super.onDestroy()
        logger.d("onDestroy")
        service = null
        recordingPackageName = "not.recording"
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        logger.d(
            "onAccessibilityEvent $foregroundPackageName ${event?.eventType?.toString(16)} ${event?.className} ${
                formatEventTime(
                    event?.eventTime!!
                )
            }"
        )
        if (event.eventType == AccessibilityEvent.TYPE_TOUCH_INTERACTION_START) {
            // タッチイベントが開始した時の処理
            logger.d("TYPE_TOUCH_INTERACTION_START $event")
        }

        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            if (event.packageName != null) {
                foregroundPackageName = event.packageName.toString()
            }
            logger.i("TYPE_WINDOW_STATE_CHANGED $foregroundPackageName")
            OverlayService.service?.adjustPassThrough()
        }

        if (event.eventType == AccessibilityEvent.TYPE_TOUCH_INTERACTION_END) {
            // タッチイベントが終了した時の処理
            logger.d("TYPE_TOUCH_INTERACTION_END $event")
        }
    }

    fun getViewForGesture(gesture: GestureDescription?): AccessibilityNodeInfo? {
        if (gesture == null) {
            return null
        }
        // Assuming the gesture is a single stroke for simplicity
        val stroke = gesture.getStroke(0)
        val path = stroke.path
        val gestureBounds = RectF()
        path.computeBounds(gestureBounds, true)
        logger.d("getViewForGesture $gestureBounds $rootInActiveWindow")
        val rootInActiveWindow = rootInActiveWindow ?: return null
        return findViewForGesture(rootInActiveWindow, gestureBounds.toRect())
    }

    private fun findViewForGesture(
        node: AccessibilityNodeInfo,
        gestureBounds: Rect
    ): AccessibilityNodeInfo? {
        val bounds = Rect()
        node.getBoundsInScreen(bounds)
        if (bounds.contains(gestureBounds)) {
            for (i in 0 until node.childCount) {
                val childNode = node.getChild(i)
                childNode?.let {
                    val result = findViewForGesture(it, gestureBounds)
                    if (result != null) {
                        return result
                    }
                }
            }
            if (node.packageName == recordingPackageName) {
                return node
            }
        }

        return null
    }

    fun testDispatch() {
        createTestClick()?.let { dispatchGesture(it, null, null) }
    }

    private fun createTestClick(): GestureDescription? {
        // for a single tap a duration of 1 ms is enough
        val DURATION = 200
        val clickPath = Path()
        clickPath.moveTo(403.97852f, 915.9043f)
        val clickStroke = GestureDescription.StrokeDescription(clickPath, 0, DURATION.toLong())
        val clickBuilder = GestureDescription.Builder()
        clickBuilder.addStroke(clickStroke)
        return clickBuilder.build()
    }

    override fun onMotionEvent(event: MotionEvent) {
        logger.d("onMotionEvent $event")
        super.onMotionEvent(event)
    }

    private fun formatEventTime(eventTime: Long): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
        val date = Date(eventTime)
        return dateFormat.format(date)
    }

    override fun onInterrupt() {
        // サービスが中断された時の処理
    }

    fun createGestureDescription(motionEvents: List<MotionEvent>): GestureDescription? {
        if (motionEvents.size < 2) return null

        val actionDown = motionEvents.firstOrNull { it.action == MotionEvent.ACTION_DOWN }
        val actionUp = motionEvents.lastOrNull { it.action == MotionEvent.ACTION_UP }

        if (actionDown == null || actionUp == null) return null

        val clickDurationThreshold = 200 // milliseconds
        if (actionUp.eventTime - actionDown.eventTime > clickDurationThreshold) return null

        val movementThreshold = 10 // pixels
        if (Math.abs(actionUp.x - actionDown.x) > movementThreshold ||
            Math.abs(actionUp.y - actionDown.y) > movementThreshold
        ) {
            return null
        }

        // Create a path for the click gesture at the position of the ACTION_DOWN event
        val clickPath = Path().apply {
            moveTo(actionDown.x, actionDown.y + statusBarHeight)
        }

        // Create a GestureDescription for the click
        val clickGesture = GestureDescription.Builder().apply {
            addStroke(GestureDescription.StrokeDescription(clickPath, 0, 100)) // 1ms duration
        }.build()

        return clickGesture
    }

    // Implement this method to calculate the duration of your gesture
//    private fun calculateGestureDuration(): Long {
//        // Example implementation
//        return motionEventList.last().eventTime - motionEventList.first().downTime
//    }

    private fun logEvent(message: String) {
        // ログ記録のためのメソッド（実際の実装に合わせてください）
        println(message)
    }

    fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = this.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = this.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }
}