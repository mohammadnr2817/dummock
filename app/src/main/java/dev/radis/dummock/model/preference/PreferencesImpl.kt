package dev.radis.dummock.model.preference

import android.content.SharedPreferences
import dev.radis.dummock.utils.constants.NumericConstants.PREFERENCES_SPEED_DEFAULT
import dev.radis.dummock.utils.constants.StringConstants.PREFERENCES_SPEED_KEY
import javax.inject.Inject

class PreferencesImpl @Inject constructor(
    private val preferences: SharedPreferences
) : Preferences {

    override fun setDefaultSpeed(speed: Int) {
        preferences.edit().putInt(PREFERENCES_SPEED_KEY, speed)
    }

    override fun getDefaultSpeed(): Int {
        return preferences.getInt(PREFERENCES_SPEED_KEY, PREFERENCES_SPEED_DEFAULT)
    }
}