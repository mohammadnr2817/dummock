package dev.radis.dummock

import android.app.Application
import dev.radis.dummock.di.application.AppComponentBuilder

class Dummock : Application() {

    override fun onCreate() {
        super.onCreate()
        AppComponentBuilder.context = this
    }

}