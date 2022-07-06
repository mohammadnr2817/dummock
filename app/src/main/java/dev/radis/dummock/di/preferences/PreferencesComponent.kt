package dev.radis.dummock.di.preferences

import android.content.SharedPreferences
import dagger.Component
import dev.radis.dummock.di.application.AppComponent
import javax.inject.Singleton

@Component(
    modules = [PreferencesModule::class],
    dependencies = [AppComponent::class]
)
@Singleton
interface PreferencesComponent {

    fun exposePreferences(): SharedPreferences

}