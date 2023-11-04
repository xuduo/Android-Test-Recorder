package com.xd.mvvm.boilerplate.data

sealed class D<T> {
    open val value: T? = null
    open val errorMessage: String? = null
    open val loading: Boolean? = false
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
