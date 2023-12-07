package com.xd.common.nav

import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavController
import com.xd.common.logger.Logger

val LocalNavController = compositionLocalOf<NavController> {
    error("No NavController provided")
}

val LocalLogger = compositionLocalOf<Logger> {
    error("No LocalLogger provided")
}
