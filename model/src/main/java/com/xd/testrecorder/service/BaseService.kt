package com.xd.testrecorder.service

import android.app.Service
import com.xd.testrecorder.accessibility.TouchAccessibilityService
import com.xd.testrecorder.overlay.OverlayService
import com.xd.testrecorder.recorder.RecorderService

fun Service.serviceFailed(): Boolean {
    return if(TouchAccessibilityService.service == null || OverlayService.service == null || RecorderService.service == null){
        stopSelf()
        true
    } else {
        false
    }
}