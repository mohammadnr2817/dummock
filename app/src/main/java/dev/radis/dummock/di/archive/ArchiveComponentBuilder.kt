package dev.radis.dummock.di.archive

import dev.radis.dummock.di.application.AppComponentBuilder
import dev.radis.dummock.di.preferences.PreferencesComponentBuilder
import dev.radis.dummock.di.repository.RepositoryComponentBuilder

object ArchiveComponentBuilder {
    private var instance: ArchiveComponent? = null

    fun getComponent(): ArchiveComponent {
        if (instance == null) instance = DaggerArchiveComponent.builder()
            .repositoryComponent(RepositoryComponentBuilder.getComponent())
            .appComponent(AppComponentBuilder.getComponent())
            .preferencesComponent(PreferencesComponentBuilder.getComponent())
            .build()
        return requireNotNull(instance)
    }
}