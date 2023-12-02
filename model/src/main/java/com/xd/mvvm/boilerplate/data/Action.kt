package com.xd.mvvm.boilerplate.data

import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.view.MotionEvent
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlin.math.round

@Entity(
    tableName = "actions",
    foreignKeys = [
        ForeignKey(
            entity = Recording::class,
            parentColumns = ["id"],
            childColumns = ["recordingId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Action(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val recordingId: Long,

    val type: String,

    val cords: List<Pair<Int, Int>>, // This needs to be converted to a storable format

    val duration: Int,

    val screenWidth: Int,

    val screenHeight: Int

) {
    fun getRatioXOnScreen(): Float {
        return cords[0].first.toFloat() / screenWidth.toFloat()
    }

    fun getRatioYOnScreen(): Float {
        return cords[0].second.toFloat() / screenHeight.toFloat()
    }

    fun toGestureDescription(): GestureDescription? {
        val gestureBuilder = GestureDescription.Builder()
        val path = Path()

        when (type) {
            "click", "long_click" -> {
                // For both click and long click, use the first coordinate
                cords.firstOrNull()?.let { (x, y) ->
                    path.moveTo(x.toFloat(), y.toFloat())
                    path.lineTo(x.toFloat(), y.toFloat())
                }
            }

            "swipe" -> {
                // For swipe, create a path from the first to the last coordinate
                cords.firstOrNull()?.let { (startX, startY) ->
                    path.moveTo(startX.toFloat(), startY.toFloat())
                    cords.lastOrNull()?.let { (endX, endY) ->
                        path.lineTo(endX.toFloat(), endY.toFloat())
                    }
                }
            }

            else -> return null
        }

        val gestureStroke = GestureDescription.StrokeDescription(path, 0, duration.toLong())
        gestureBuilder.addStroke(gestureStroke)
        return gestureBuilder.build()
    }
}

fun convertMotionEventsToAction(
    motionEvents: List<MotionEvent>,
    recordingId: Long,
    width: Int,
    height: Int
): Action? {
    if (motionEvents.isEmpty()) return null

    val startEvent = motionEvents.first()
    val endEvent = motionEvents.last()

    // Check if the start is a down event and the end is an up event
    if (startEvent.action != MotionEvent.ACTION_DOWN || endEvent.action != MotionEvent.ACTION_UP) {
        return null
    }

    val duration = (endEvent.eventTime - startEvent.downTime).toInt()

    // Calculate total movement and accumulate all coordinates
    var totalDistanceX = 0f
    var totalDistanceY = 0f
    val cords = mutableListOf<Pair<Int, Int>>()

    for (i in 1 until motionEvents.size) {
        val prevEvent = motionEvents[i - 1]
        val currEvent = motionEvents[i]

        totalDistanceX += Math.abs(currEvent.x - prevEvent.x)
        totalDistanceY += Math.abs(currEvent.y - prevEvent.y)

        cords.add(Pair(round(currEvent.x).toInt(), round(currEvent.y).toInt()))
    }

    return when {
        totalDistanceX > 20 || totalDistanceY > 20 -> // Swipe
            Action(
                recordingId = recordingId,
                type = "swipe",
                cords = cords,
                duration = duration,
                screenWidth = width,
                screenHeight = height
            )

        duration < 500 -> // Click
            Action(
                recordingId = recordingId,
                type = "click",
                cords = listOf(Pair(round(startEvent.x).toInt(), round(startEvent.y).toInt())),
                duration = 10,
                screenWidth = width,
                screenHeight = height
            )

        else -> // Long Click
            Action(
                recordingId = recordingId,
                type = "long_click",
                cords = listOf(Pair(round(startEvent.x).toInt(), round(startEvent.y).toInt())),
                duration = duration,
                screenWidth = width,
                screenHeight = height
            )
    }
}
