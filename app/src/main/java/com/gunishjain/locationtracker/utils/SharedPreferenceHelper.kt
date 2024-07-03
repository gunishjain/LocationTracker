package com.gunishjain.locationtracker.utils

import android.content.Context

class SharedPreferenceHelper(private val context: Context) {

    companion object {
        private const val PREF_KEY = "PREF_KEY"
    }

    fun savedStringData(key: String,data: String?){
        val sharedPreferences = context.getSharedPreferences(PREF_KEY,Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(key,data).apply()
    }

    fun getStringData(key: String) : String? {
        val sharedPreferences = context.getSharedPreferences(PREF_KEY,Context.MODE_PRIVATE)
        return sharedPreferences.getString(key,null)
    }

    fun clearPreference() {
        val sharedPreferenceHelper = context.getSharedPreferences(PREF_KEY,Context.MODE_PRIVATE)
        sharedPreferenceHelper.edit().clear().apply()
    }


}