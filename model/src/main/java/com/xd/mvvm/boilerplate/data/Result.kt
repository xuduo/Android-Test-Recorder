package com.xd.mvvm.boilerplate.data

import androidx.lifecycle.MutableLiveData

sealed class D<T> {
    open val value: T? = null
    open val errorMessage: String? = null
    open val loading: Boolean? = false

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as D<*>

        if (value != other.value) return false
        if (errorMessage != other.errorMessage) return false
        if (loading != other.loading) return false

        return true
    }

    override fun hashCode(): Int {
        var result = value?.hashCode() ?: 0
        result = 31 * result + (errorMessage?.hashCode() ?: 0)
        result = 31 * result + (loading?.hashCode() ?: 0)
        return result
    }

}

data class Success<T>(override val value: T) : D<T>() {
    //    override val errorMessage: String? = null
    override val loading: Boolean = false
}

data class Err<T>(override val errorMessage: String) : D<T>() {
    //    override val value: T? = null
    override val loading: Boolean = false
}

data class Loading<T>(override val value: T? = null) : D<T>() {
    //    override val errorMessage: String? = null
    override val loading: Boolean = true
}

fun <T> nullToLoading(value: T?): D<T> {
    return if(value == null){
        Loading(null)
    } else {
        Success(value)
    }
}

fun <T> MutableLiveData<T>.postNoneEqual(data: T): Boolean {
    return if (this.value != data) {
        postValue(data)
        true
    } else {
        false
    }
}

