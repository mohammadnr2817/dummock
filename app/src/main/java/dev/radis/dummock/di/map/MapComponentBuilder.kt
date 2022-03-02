package dev.radis.dummock.di.map

import dev.radis.dummock.di.repository.RepositoryComponentBuilder

object MapComponentBuilder {
    private var instance: MapComponent? = null

    fun getComponent(): MapComponent {
        if (instance == null) instance = DaggerMapComponent.builder()
            .repositoryComponent(RepositoryComponentBuilder.getComponent())
            .build()
        return requireNotNull(instance)
    }
}