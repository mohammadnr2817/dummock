package dev.radis.dummock.di.application

import android.app.Application

object AppComponentBuilder {
    private var instance: AppComponent? = null
    lateinit var context: Application

    fun getComponent(): AppComponent {
        if (instance == null) instance =
            DaggerAppComponent.builder()
                .setApplication(context)
                .build()
        return requireNotNull(instance)
    }
}