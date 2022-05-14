package dev.radis.dummock

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import dev.radis.dummock.di.application.AppComponentBuilder

class Dummock : Application() {

    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        AppComponentBuilder.context = this
    }

}