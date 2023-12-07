package com.xd.testrecorder.sharedpref

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData


class BooleanSharedPreferenceLiveData(
    private val sharedPreferences: SharedPreferences,
    private val key: String,
    private val defaultValue: Boolean
) : MutableLiveData<Boolean>() {

    init {
        value = sharedPreferences.getBoolean(key, defaultValue)
    }

    private val preferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { _, updatedKey ->
            if (updatedKey == key) {
                value = sharedPreferences.getBoolean(key, defaultValue)
            }
        }

    override fun onActive() {
        super.onActive()
        sharedPreferences.registerOnSharedPreferenceChangeListener(preferenceChangeListener)
    }

    override fun onInactive() {
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(preferenceChangeListener)
        super.onInactive()
    }

    override fun setValue(value: Boolean?) {
        super.setValue(value)
        if (value != null) {
            sharedPreferences.edit().putBoolean(key, value).apply()
        }
    }

    fun postValueAndSave(value: Boolean) {
        setValue(value)
    }

    fun toggle() {
        setValue(!value!!)
    }

}
