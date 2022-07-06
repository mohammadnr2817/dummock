package dev.radis.dummock.di.preferences

import dev.radis.dummock.di.application.AppComponentBuilder

object PreferencesComponentBuilder {
    private var instance: PreferencesComponent? = null

    fun getComponent(): PreferencesComponent {
        if (instance == null) instance =
            DaggerPreferencesComponent.builder()
                .appComponent(AppComponentBuilder.getComponent())
                .preferencesModule(PreferencesModule())
                .build()
        return requireNotNull(instance)
    }
}