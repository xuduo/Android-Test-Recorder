package com.xd.testrecorder.accessibility

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.view.MotionEvent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.core.graphics.toRect
import com.xd.common.logger.Logger
import com.xd.testrecorder.overlay.OverlayService
import com.xd.testrecorder.service.serviceFailed
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

        fun getStatusBarHeight(): Int {
            return service?.getStatusBarHeight() ?: 0
        }

        fun getViewForGesture(gesture: GestureDescription?): Pair<AccessibilityNodeInfo?, AccessibilityNodeInfo?> {
            return service?.getViewForGesture(gesture) ?: Pair(null, null)
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
        logger.i("onDestroy")
        service = null
        recordingPackageName = "not.recording"
        OverlayService.service?.serviceFailed()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        logger.v(
            "onAccessibilityEvent $foregroundPackageName ${event?.eventType?.toString(16)} ${event?.className} ${
                formatEventTime(
                    event?.eventTime!!
                )
            }"
        )
    }

    fun getViewForGesture(gesture: GestureDescription?): Pair<AccessibilityNodeInfo?, AccessibilityNodeInfo?> {
        if (gesture == null) {
            return Pair(null, null)
        }
        // Assuming the gesture is a single stroke for simplicity
        val stroke = gesture.getStroke(0)
        val path = stroke.path
        val gestureBounds = RectF()
        path.computeBounds(gestureBounds, true)
        val rootInActiveWindow = rootInActiveWindow ?: return Pair(null, null)
        val click = findViewForGesture(rootInActiveWindow, gestureBounds.toRect())
        var feature = findViewForGesture(click, null, true)
        if (feature == null) {
            feature = findViewForGesture(
                click, null,
                checkHasDesc = false,
                checkHasText = true
            )
        }
        logger.d("getViewForGesture has feature:${feature !== click} $gestureBounds $rootInActiveWindow")
        return Pair(click, feature)
    }

    private fun findViewForGesture(
        node: AccessibilityNodeInfo?,
        bounds: Rect?,
        checkHasDesc: Boolean = false,
        checkHasText: Boolean = false
    ): AccessibilityNodeInfo? {
        // Base case: if the node is null or not visible, return null
        if (node == null || node.isVisibleToUser.not()) {
            return null
        }

        // Check if the node is clickable and its bounds contain the specified bounds
        val nodeBounds = Rect()
        node.getBoundsInScreen(nodeBounds)
        var valid = true
        if (bounds != null) {
            valid =
                node.isClickable && nodeBounds.contains(bounds) && node.packageName == recordingPackageName
        }
        if (checkHasDesc) {
            valid = valid && node.contentDescription?.isNotEmpty() == true
        }
        if (checkHasText) {
            valid = valid && node.text?.isNotEmpty() == true
        }

        var bestMatch: AccessibilityNodeInfo? = if (valid) node else null

        // Recursively search child nodes
        for (i in 0 until node.childCount) {
            node.getChild(i)?.let { childNode ->
                val foundNode = findViewForGesture(childNode, bounds)
                if (foundNode != null) {
                    // Compare the bounds to find the smallest node
                    val foundNodeBounds = Rect()
                    foundNode.getBoundsInScreen(foundNodeBounds)
                    // find the smallest or the shortest text
                    if (bestMatch == null || foundNodeBounds.width() * foundNodeBounds.height() < nodeBounds.width() * nodeBounds.height() ||
                        (foundNode.contentDescription?.length
                            ?: 0) < (bestMatch?.contentDescription?.length ?: 0) ||
                        (foundNode.text?.length ?: 0) < (bestMatch?.text?.length ?: 0)
                    ) {
                        bestMatch = foundNode
                    }
                }
            }
        }

        return bestMatch
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

    fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = this.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            result = this.resources.getDimensionPixelSize(resourceId)
        }
        return result
    }
}