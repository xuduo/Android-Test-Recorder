package com.xd.mvvm.testrecorder.coroutine

import androidx.lifecycle.LiveDataScope
import androidx.lifecycle.liveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun io(block: suspend CoroutineScope.() -> Unit) {
    CoroutineScope(Dispatchers.IO).launch {
        block()
    }
}

fun <T> ioLiveData(block: suspend LiveDataScope<T>.() -> Unit) = liveData(
    context = Dispatchers.IO,
    block = block
)

fun main(block: suspend CoroutineScope.() -> Unit) {
    CoroutineScope(Dispatchers.Main).launch {
        block()
    }
}
