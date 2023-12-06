package com.xd.mvvm.testrecorder.recorder

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.graphics.SurfaceTexture
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.IBinder
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.xd.mvvm.testrecorder.logger.Logger
import com.xd.mvvm.testrecorder.model.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

//@AndroidEntryPoint
class RecorderService : Service() {

    companion object {
        var service: RecorderService? = null
        var startMediaIntent: Intent? = null
        var resultCode = 0
        const val ID_MEDIA_PROJECTION_SERVICE = 101

        fun isRecording():Boolean {
            return service?.recording == true
        }

        fun getLatestImage(): Image? {
            val latest = service?.latestImage
            service?.latestImage = null
            return latest
        }
    }

    private val logger = Logger("RecorderService")
    private var display: VirtualDisplay? = null
    private var surfaceTexture: SurfaceTexture? = null
    private var mediaProjection: MediaProjection? = null
    private var imageReader: ImageReader? = null
    private var recording = false
    var latestImage: Image? = null

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        // Start the service as a foreground service
        val notification =
            createNotification() // Implement this method to create a proper notification
        startForeground(ID_MEDIA_PROJECTION_SERVICE, notification)
        logger.i("RecorderService onStartCommand")
        return START_NOT_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        logger.i("RecorderService onCreate")
        service = this
        CoroutineScope(Dispatchers.Main).launch {
            delay(1000L)
            startRecording()
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun startRecording() {
        val mediaProjectionManager = ContextCompat.getSystemService(
            applicationContext, MediaProjectionManager::class.java
        ) as MediaProjectionManager

        surfaceTexture = SurfaceTexture(1)
        surfaceTexture?.setOnFrameAvailableListener {
            // Process frame here
            logger.d("setOnFrameAvailableListener")
        }

        logger.d("start recording $resultCode $startMediaIntent")

        mediaProjection =
            mediaProjectionManager.getMediaProjection(resultCode, startMediaIntent!!)

        mediaProjection?.registerCallback(object : MediaProjection.Callback() {
            override fun onStop() {
                display?.release()
                logger.d("MediaProjection.Callback onStop")
            }
        }, null)

        val metrics = getScreenMetrics()

        val width = metrics.widthPixels // Screen width in pixels
        val height = metrics.heightPixels // Screen height in pixels
        val dpi = metrics.densityDpi // Screen density in DPI

        imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 10)

        display = mediaProjection?.createVirtualDisplay(
            "ScreenRecordService",
            width, height, dpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader?.surface, null, null
        )

        imageReader?.setOnImageAvailableListener({ reader ->
            // Handle the image available
            val image = reader.acquireLatestImage()
            if (image != null) {
                latestImage?.close()
                logger.v("imageReader on frame ${image.format}")
                latestImage = image
            }
        }, null)
        recording = true
    }

    private fun getScreenMetrics(): DisplayMetrics {
        val metrics = DisplayMetrics()
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getRealMetrics(metrics)
        return metrics
    }

    override fun onDestroy() {
        super.onDestroy()
        service = null
        latestImage?.close()
        latestImage = null
    }

    private fun convertNanosecondsToReadable(timestamp: Long): String {
        val instant = Instant.ofEpochSecond(0, timestamp)
        val formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").withZone(ZoneId.systemDefault())
        return formatter.format(instant)
    }

    private fun createNotification(): Notification {
        val notificationChannelId = "media_projection_service_channel"

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Media Projection Service"
            val descriptionText = "This channel is used for media projection service notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(notificationChannelId, name, importance).apply {
                description = descriptionText
            }

            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Build the notification
        return NotificationCompat.Builder(this, notificationChannelId)
            .setContentTitle("Screen Recording")
            .setContentText("Recording your screen.")
            .setSmallIcon(R.mipmap.ic_launcher) // Replace with your own icon
            // Add more properties as needed
            .build()
    }


}