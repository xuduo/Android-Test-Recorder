package com.xd.common.logger

import android.util.Log

class Logger(private val tag: String) {

    fun d(message: String) {
        Log.d(tag, message)
    }

    fun v(message: String) {
        Log.v(tag, message)
    }

    fun d(message: String, t: Throwable) {
        Log.d(tag, message, t)
    }

    fun i(message: String) {
        Log.i(tag, message)
    }

    fun i(message: String, t: Throwable) {
        Log.i(tag, message, t)
    }

    fun w(message: String) {
        Log.w(tag, message)
    }

    fun w(message: String, t: Throwable) {
        Log.w(tag, message, t)
    }

    fun e(message: String) {
        Log.e(tag, message)
    }

    fun e(message: String, t: Throwable) {
        Log.e(tag, message, t)
    }
}

val L = Logger("Default")
