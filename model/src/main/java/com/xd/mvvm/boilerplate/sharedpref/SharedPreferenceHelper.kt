package com.xd.mvvm.boilerplate.sharedpref

import android.content.Context
import android.content.SharedPreferences
import com.squareup.moshi.Moshi
import com.xd.mvvm.boilerplate.logger.Logger
import javax.inject.Inject

class SharedPreferencesHelper @Inject constructor(
    private val context: Context,
    name: String,
    private val moshi: Moshi
) {
    private val logger = Logger("SharedPreferencesHelper-$name")
    private val sharedPreferences: SharedPreferences by lazy {
        val timestamp = System.currentTimeMillis()
        val sp = context.getSharedPreferences(name, Context.MODE_PRIVATE)
        logger.d("getSharedPreferences $name ${System.currentTimeMillis() - timestamp}")
        sp
    }

    fun getBooleanLiveData(
        key: String,
        defaultValue: Boolean = false
    ): BooleanSharedPreferenceLiveData {
        return BooleanSharedPreferenceLiveData(sharedPreferences, key, defaultValue)
    }

    // String
    fun putString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getString(key: String, defaultValue: String? = null): String? {
        return sharedPreferences.getString(key, defaultValue)
    }

    fun <T> putObject(key: String, value: T?, clazz: Class<T>) {
        if (value == null) {
            return
        }
        val adapter = moshi.adapter(clazz)
        try {
            val jsonString = adapter.toJson(value)
            sharedPreferences.edit().putString(key, jsonString).apply()
        } catch (e: Exception) {
            logger.e("putObject error occurred: ${e.message}")
        }
    }

    fun <T> getObject(key: String, clazz: Class<T>, defaultValue: T? = null): T? {
        val timestamp = System.currentTimeMillis()
        val jsonString = sharedPreferences.getString(key, null)
        try {
            val adapter = moshi.adapter(clazz)
            if (jsonString != null) {
                logger.d("getObject $key ${System.currentTimeMillis() - timestamp}")
                return adapter.fromJson(jsonString)
            }
        } catch (e: Exception) {
            logger.e("getObject error occurred: ${e.message}")
        }
        return null
    }

    // Int
    fun putInt(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
    }

    fun getInt(key: String, defaultValue: Int = 0): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }

    // Float
    fun putFloat(key: String, value: Float) {
        sharedPreferences.edit().putFloat(key, value).apply()
    }

    fun getFloat(key: String, defaultValue: Float = 0f): Float {
        return sharedPreferences.getFloat(key, defaultValue)
    }

    // Long
    fun putLong(key: String, value: Long) {
        sharedPreferences.edit().putLong(key, value).apply()
    }

    fun getLong(key: String, defaultValue: Long = 0L): Long {
        return sharedPreferences.getLong(key, defaultValue)
    }

    // Boolean
    fun putBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    // Remove key
    fun remove(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }

    // Clear all entries
    fun clear() {
        sharedPreferences.edit().clear().apply()
    }
}
