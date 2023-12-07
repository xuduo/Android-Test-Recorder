package com.xd.testrecorder.coroutine

import androidx.lifecycle.LiveDataScope
import androidx.lifecycle.MutableLiveData
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

fun <T> ioMutable(block: suspend CoroutineScope.() -> T): MutableLiveData<T> {
    val mutable = MutableLiveData<T>()
    CoroutineScope(Dispatchers.IO).launch {
        val value = block()
        if (value != null) {
            mutable.postValue(value)
        }
    }
    return mutable
}

fun main(block: suspend CoroutineScope.() -> Unit) {
    CoroutineScope(Dispatchers.Main).launch {
        block()
    }
}
