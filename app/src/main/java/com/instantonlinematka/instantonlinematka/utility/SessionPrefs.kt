package com.instantonlinematka.instantonlinematka.utility

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences

@SuppressLint("CommitPrefEdits")
class SessionPrefs(context: Context) {

    // Preference Instances
    lateinit var prefs: SharedPreferences

    lateinit var prefsEdit: SharedPreferences.Editor

    // Preferences Name
    val PREFS_NAME = "MATKA_PREFS"

    // Initilize Preferences
    init {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefsEdit = prefs.edit()
    }

    // Installing Values to Shared Preferences
    // Adding String Value
    fun addString(key: String, value: String) {
        prefsEdit.putString(key, value)
        prefsEdit.commit()
    }

    // Adding Integer Value
    fun addInteger(key: String, value: Int) {
        prefsEdit.putInt(key, value)
        prefsEdit.commit()
    }

    // Adding Float Value
    fun addFloat(key: String, value: Float) {
        prefsEdit.putFloat(key, value)
        prefsEdit.commit()
    }

    // Adding Boolean Value
    fun addBoolean(key: String, value: Boolean) {
        prefsEdit.putBoolean(key, value)
        prefsEdit.commit()
    }


    // Retrieving Values to Shared Preferences
    // Adding String Value
    fun getString(key: String): String {
        return prefs.getString(key, "").toString()
    }

    // Adding Integer Value
    fun getInteger(key: String): Int {
        return prefs.getInt(key, 0)
    }

    // Adding Float Value
    fun getFloat(key: String): Float {
        return prefs.getFloat(key, 0.0f)
    }

    // Adding Boolean Value
    fun getBoolean(key: String): Boolean {
        return prefs.getBoolean(key, false)
    }

    // Remove Single Data
    fun removeData(key: String?) {
        prefsEdit.remove(key)
        prefsEdit.commit()
    }

    // Remove All Data
    fun removeAll() {
        prefsEdit.clear()
        prefsEdit.commit()
    }

}