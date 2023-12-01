package com.xd.mvvm.boilerplate.recording

import android.app.Application
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.graphics.Bitmap
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.xd.mvvm.boilerplate.accessibility.TouchAccessibilityService
import com.xd.mvvm.boilerplate.logger.Logger
import com.xd.mvvm.boilerplate.overlay.OverlayService
import com.xd.mvvm.boilerplate.recorder.RecorderService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject

data class AppInfo(
    val icon: Bitmap,
    val packageName: String,
    val appName: String,
    val lastTimeUsed: Long
)

@HiltViewModel
class RecordingViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val logger = Logger("RecordingViewModel")

    init {
        logger.d("RecordingViewModel.init")
    }

    fun isAccessibilityServiceEnabled(): Boolean {
        val expectedId = context.packageName + "/" + TouchAccessibilityService::class.java.name
        val enabledServicesSetting = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false

        val enabledServices = TextUtils.SimpleStringSplitter(':').apply {
            setString(enabledServicesSetting)
        }

        while (enabledServices.hasNext()) {
            val enabledService = enabledServices.next()
            if (expectedId == enabledService) {
                return true
            }
        }

        return false
    }

    fun getAppsSortedByRecentUsage(): List<AppInfo> {
        val pm = context.packageManager
        val usageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val endTime = System.currentTimeMillis()
        val startTime = endTime - (1000 * 60 * 60 * 24) // last 24 hours

        val usageStatsList = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_MONTHLY,
            startTime,
            endTime
        )
        val lastTimeUsedMap = usageStatsList.associate { it.packageName to it.lastTimeUsed }

        logger.d("lastTimeUsedMap $lastTimeUsedMap")

        return pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .mapNotNull { appInfo ->
                try {
                    val iconDrawable = pm.getApplicationIcon(appInfo.packageName)
                    val iconBitmap = (iconDrawable as? BitmapDrawable)?.bitmap
                    val appName = pm.getApplicationLabel(appInfo).toString()
                    val lastTimeUsed = lastTimeUsedMap[appInfo.packageName] ?: 0
                    val intent =
                        context.packageManager.getLaunchIntentForPackage(appInfo.packageName)
                    if (iconBitmap != null && intent != null) {
                        AppInfo(iconBitmap, appInfo.packageName, appName, lastTimeUsed)
                    } else null
                } catch (e: PackageManager.NameNotFoundException) {
                    null
                }
            }
            .sortedWith(compareByDescending<AppInfo> { it.lastTimeUsed > 0 }.thenBy { it.appName })
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

    fun startRecording(launcher: ManagedActivityResultLauncher<Intent, ActivityResult>) {
        val mediaProjectionManager = ContextCompat.getSystemService(
            context, MediaProjectionManager::class.java
        ) as MediaProjectionManager

        val screenCaptureIntent = mediaProjectionManager.createScreenCaptureIntent()
        launcher.launch(screenCaptureIntent)
    }

    fun handleScreenCaptureResult(resultCode: Int, data: Intent?) {
        val serviceIntent = Intent(context, RecorderService::class.java)
        RecorderService.startMediaIntent = data
        RecorderService.resultCode = resultCode
        ContextCompat.startForegroundService(context, serviceIntent)
    }
}
