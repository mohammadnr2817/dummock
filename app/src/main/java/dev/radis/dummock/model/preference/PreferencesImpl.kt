package dev.radis.dummock.model.preference

import android.content.SharedPreferences
import dev.radis.dummock.utils.constants.NumericConstants.DEFAULT_SPEED_KMH
import dev.radis.dummock.utils.constants.StringConstants.PREFERENCES_SPEED_KEY
import javax.inject.Inject

class PreferencesImpl @Inject constructor(
    private val preferences: SharedPreferences
) : Preferences {

    override suspend fun setDefaultSpeed(speed: Int) {
        preferences.edit().putInt(PREFERENCES_SPEED_KEY, speed).apply()
    }

    override suspend fun getDefaultSpeed(): Int {
        return preferences.getInt(PREFERENCES_SPEED_KEY, DEFAULT_SPEED_KMH)
    }
}