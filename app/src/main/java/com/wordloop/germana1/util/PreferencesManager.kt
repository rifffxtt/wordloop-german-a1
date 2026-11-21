package com.wordloop.germana1.util

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesManager @Inject constructor(
    private val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("wordloop_prefs", Context.MODE_PRIVATE)

    private val KEY_DAILY_GOAL = "daily_goal"
    private val KEY_TTS_RATE = "tts_rate"
    private val KEY_SEED_DATA_LOADED = "seed_data_loaded"
    private val KEY_FIRST_LAUNCH = "first_launch"

    fun getDailyGoal(): Int {
        return prefs.getInt(KEY_DAILY_GOAL, 15)
    }

    fun setDailyGoal(goal: Int) {
        prefs.edit().putInt(KEY_DAILY_GOAL, goal).apply()
    }

    fun getTtsRate(): Float {
        return prefs.getFloat(KEY_TTS_RATE, 1.0f)
    }

    fun setTtsRate(rate: Float) {
        prefs.edit().putFloat(KEY_TTS_RATE, rate).apply()
    }

    fun isSeedDataLoaded(): Boolean {
        return prefs.getBoolean(KEY_SEED_DATA_LOADED, false)
    }

    fun setSeedDataLoaded(loaded: Boolean) {
        prefs.edit().putBoolean(KEY_SEED_DATA_LOADED, loaded).apply()
    }

    fun isFirstLaunch(): Boolean {
        return prefs.getBoolean(KEY_FIRST_LAUNCH, true)
    }

    fun setFirstLaunchComplete() {
        prefs.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply()
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }
}