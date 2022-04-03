package dev.radis.dummock.di.map

import dev.radis.dummock.di.application.AppComponentBuilder
import dev.radis.dummock.di.repository.RepositoryComponentBuilder

object MapComponentBuilder {
    private var instance: MapComponent? = null

    fun getComponent(): MapComponent {
        if (instance == null) instance = DaggerMapComponent.builder()
            .repositoryComponent(RepositoryComponentBuilder.getComponent())
            .appComponent(AppComponentBuilder.getComponent())
            .build()
        return requireNotNull(instance)
    }
}