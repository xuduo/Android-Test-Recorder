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

inline fun <T1, T2, R> let2(value1: T1?, value2: T2?, block: (T1, T2) -> R): R? {
    return if (value1 != null && value2 != null) {
        block(value1, value2)
    } else {
        null
    }
}

inline fun <T1, T2, T3, R> let3(value1: T1?, value2: T2?, value3: T3?, block: (T1, T2, T3) -> R): R? {
    return if (value1 != null && value2 != null && value3 != null) {
        block(value1, value2, value3)
    } else {
        null
    }
}


fun <T> ioLiveData(block: suspend LiveDataScope<T>.() -> Unit) = liveData(
    context = Dispatchers.IO,
    block = block
)

fun <T> ioMutable(block: CoroutineScope.() -> T): MutableLiveData<T> {
    val mutable = MutableLiveData<T>()
    CoroutineScope(Dispatchers.IO).launch {
        block()?.let {
            mutable.postValue(it)
        }
    }
    return mutable
}

fun main(block: suspend CoroutineScope.() -> Unit) {
    CoroutineScope(Dispatchers.Main).launch {
        block()
    }
}
