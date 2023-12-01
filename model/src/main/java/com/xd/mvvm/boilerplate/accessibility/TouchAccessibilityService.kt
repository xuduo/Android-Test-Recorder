package com.xd.mvvm.boilerplate.accessibility

import android.accessibilityservice.AccessibilityGestureEvent
import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.view.MotionEvent
import android.view.accessibility.AccessibilityEvent
import com.xd.mvvm.boilerplate.logger.Logger
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TouchAccessibilityService : AccessibilityService() {

    private val logger = Logger("TouchAccessibilityService")
    private var statusBarHeight = 0

    companion object {
        var service: TouchAccessibilityService? = null

        fun dispatchGesture(
            motionEvents: MutableList<MotionEvent>,
            callback: GestureResultCallback
        ) {
            service?.let { service ->
                val gesture = service.createGestureDescription(motionEvents)
                motionEvents.clear()
                gesture?.let { gesture ->
                    service.dispatchGesture(gesture, callback, null)
                }
            }
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
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        logger.d(
            "onAccessibilityEvent ${event?.eventType?.toString(16)} ${event?.className} ${
                formatEventTime(
                    event?.eventTime!!
                )
            }"
        )
        if (event?.eventType == AccessibilityEvent.TYPE_TOUCH_INTERACTION_START) {
            // タッチイベントが開始した時の処理
            logger.d("TYPE_TOUCH_INTERACTION_START $event")
        }

        if (event?.eventType == AccessibilityEvent.TYPE_TOUCH_INTERACTION_END) {
            // タッチイベントが終了した時の処理
            logger.d("TYPE_TOUCH_INTERACTION_END $event")
        }
    }

//    override fun onGesture(gestureEvent: AccessibilityGestureEvent): Boolean {
//        logger.d("onGesture $gestureEvent")
//        dispatchGesture(createGestureDescriptionFromEvent(gestureEvent),null,null)
//        return super.onGesture(gestureEvent)
//    }

    private fun createGestureDescriptionFromEvent(event: AccessibilityGestureEvent): GestureDescription {
        // Example: Assuming the event is a swipe gesture and you have extracted the necessary points and timing
        val builder = GestureDescription.Builder()

//        event.motionEvents.forEach { motionEvent ->
//            val path = Path().apply {
//                moveTo(motionEvent.x, motionEvent.y)
//                // Add more points to the path if needed
//            }
//
//            val stroke = GestureDescription.StrokeDescription(
//                path, motionEvent.downTime, motionEvent.eventTime - motionEvent.downTime
//            )
//            builder.addStroke(stroke)
//        }

        return builder.build()
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

    private fun createGestureDescription(motionEvents: List<MotionEvent>): GestureDescription? {
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