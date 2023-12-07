package com.xd.testrecorder.recording

import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.projection.MediaProjectionManager
import android.provider.Settings
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xd.common.coroutine.io
import com.xd.common.data.postNoneEqual
import com.xd.common.logger.Logger
import com.xd.testrecorder.accessibility.TouchAccessibilityService
import com.xd.testrecorder.dao.ActionDao
import com.xd.testrecorder.dao.ActionImageDao
import com.xd.testrecorder.dao.RecordingDao
import com.xd.testrecorder.data.Action
import com.xd.testrecorder.data.ActionImage
import com.xd.testrecorder.data.Recording
import com.xd.testrecorder.data.RecordingWithActionCount
import com.xd.testrecorder.overlay.OverlayService
import com.xd.testrecorder.recorder.RecorderService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject


data class AppInfo(
    val icon: Bitmap,
    val packageName: String,
    val appName: String,
    val lastTimeUsed: Long
)

@HiltViewModel
class RecordingViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val recordingDao: RecordingDao,
    private val actionDao: ActionDao,
    private val actionImageDao: ActionImageDao,
) : ViewModel() {
    private val logger = Logger("RecordingViewModel")
    val apps by lazy {
        viewModelScope.launch { queryAppsSortedByRecentUsage() }
        MutableLiveData<List<AppInfo>>()
    }

    init {
        logger.d("RecordingViewModel.init")
    }

    fun isAccessibilityServiceEnabled(): Boolean {
        return TouchAccessibilityService.service != null
    }

    private fun queryAppsSortedByRecentUsage() {
        io {
            val pm = context.packageManager
            val usageStatsManager =
                context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val endTime = System.currentTimeMillis()
            val startTime = endTime - (1000 * 60 * 60 * 24 * 30L) // last 30 days

            val usageStatsList = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_MONTHLY,
                startTime,
                endTime
            )
            val lastTimeUsedMap = usageStatsList.associate { it.packageName to it.lastTimeUsed }

            logger.d("lastTimeUsedMap $lastTimeUsedMap")

            val list = pm.getInstalledApplications(PackageManager.GET_META_DATA)
                .mapNotNull { appInfo ->
                    try {
                        val iconDrawable = pm.getApplicationIcon(appInfo.packageName)
                        val iconBitmap = drawableToBitmap(iconDrawable)
                        val appName = pm.getApplicationLabel(appInfo).toString()
                        val lastTimeUsed = lastTimeUsedMap[appInfo.packageName] ?: 0
                        val intent =
                            context.packageManager.getLaunchIntentForPackage(appInfo.packageName)
                        logger.v("getLaunchIntentForPackage ${appInfo.packageName} ${intent != null} ${iconBitmap != null} ${iconDrawable != null}")
                        if (iconBitmap != null && intent != null) {
                            AppInfo(iconBitmap, appInfo.packageName, appName, lastTimeUsed)
                        } else null
                    } catch (e: PackageManager.NameNotFoundException) {
                        null
                    }
                }
                .sortedWith(
                    compareByDescending<AppInfo> { it.lastTimeUsed > 0 }
                        .thenComparator { a, b -> customComparator.compare(a.appName, b.appName) }
                )
            apps.postNoneEqual(list)
        }
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap? {
        // If the drawable is already a BitmapDrawable, just get the bitmap
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }

        // Create a Bitmap with the same dimensions as the Drawable
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )

        // Draw the Drawable onto the Bitmap
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)

        return bitmap
    }

    fun canShowOverlay(): Boolean {
        val can = Settings.canDrawOverlays(context)
        if (can) {
            context.startService(Intent(context, OverlayService::class.java))
        }
        return can
    }

    fun isRecording(): Boolean {
        return RecorderService.isRecording()
    }

    fun startCapture(launcher: ManagedActivityResultLauncher<Intent, ActivityResult>) {
        val mediaProjectionManager = ContextCompat.getSystemService(
            context, MediaProjectionManager::class.java
        ) as MediaProjectionManager

        val screenCaptureIntent = mediaProjectionManager.createScreenCaptureIntent()
        launcher.launch(screenCaptureIntent)
    }

    fun startRecording(appInfo: AppInfo) {
        val recording = Recording().apply {
            this.name = appInfo.appName
            this.packageName = appInfo.packageName
            this.setIcon(appInfo.icon)
        }
        io {
            val recordingId = recordingDao.insertRecording(recording)
            TouchAccessibilityService.service?.recordingPackageName = appInfo.packageName
            OverlayService.service?.recordingId = recordingId
        }
    }

    fun getAllRecordings(): LiveData<List<RecordingWithActionCount>> {
        return recordingDao.getRecordingsWithActionCount()
    }

    fun getRecordingById(id: Long): LiveData<Recording> {
        return recordingDao.getRecordingById(id)
    }

    fun getActionsByRecordingId(recordingId: Long): LiveData<List<Action>> {
        return actionDao.getActionsByRecordingId(recordingId = recordingId)
    }

    fun handleScreenCaptureResult(resultCode: Int, data: Intent?) {
        val serviceIntent = Intent(context, RecorderService::class.java)
        RecorderService.startMediaIntent = data
        RecorderService.resultCode = resultCode
        ContextCompat.startForegroundService(context, serviceIntent)
    }

    fun getActionImage(actionId: Long): LiveData<ActionImage> {
        return actionImageDao.getActionImageByActionId(actionId)
    }

    val customComparator = Comparator<String> { str1, str2 ->
        str1.zip(str2).forEach { (c1, c2) ->
            if (c1.isASCII() && !c2.isASCII()) return@Comparator 1
            if (!c1.isASCII() && c2.isASCII()) return@Comparator -1
            if (c1 != c2) return@Comparator c1.compareTo(c2)
        }
        str1.length.compareTo(str2.length)
    }

    private fun Char.isASCII(): Boolean = this in '\u0000'..'\u007F'
}

