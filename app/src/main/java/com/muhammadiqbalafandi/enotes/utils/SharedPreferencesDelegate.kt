package com.muhammadiqbalafandi.enotes.utils

import android.app.Activity
import android.content.Context
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class SharedPreferencesDelegate<T>(
    context: Context,
    private val key: String,
    private val defaultValue: T
) : ReadWriteProperty<Any?, T> {

    companion object {
        private const val PREFS = "PREFS"
    }

    private val sharedPreferences = context.getSharedPreferences(PREFS, Activity.MODE_PRIVATE)

    override fun getValue(thisRef: Any?, property: KProperty<*>): T =
        findPreference(key, defaultValue)

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) =
        putPreference(key, value)

    @Suppress("UNCHECKED_CAST")
    private fun <T> findPreference(key: String, default: T): T = with(sharedPreferences) {
        when (default) {
            is String -> getString(key, default) as T
            is Boolean -> getBoolean(key, default) as T
            is Int -> getInt(key, default) as T
            else -> throw IllegalArgumentException("This type cannot be saved into Preferences")
        }
    }

    private fun <T> putPreference(key: String, value: T) = with(sharedPreferences.edit()) {
        when (value) {
            is String -> putString(key, value)
            is Boolean -> putBoolean(key, value)
            is Int -> putInt(key, value)
            else -> throw IllegalArgumentException("This type cannot be saved into Preferences")
        }.apply()
    }
}