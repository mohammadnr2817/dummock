package dev.radis.dummock.di.repository

import dev.radis.dummock.di.application.AppComponentBuilder
import dev.radis.dummock.di.networking.NetworkingComponentBuilder

object RepositoryComponentBuilder {
    private var instance: RepositoryComponent? = null

    fun getComponent(): RepositoryComponent {
        if (instance == null) instance =
            DaggerRepositoryComponent.builder()
                .networkingComponent(NetworkingComponentBuilder.getComponent())
                .appComponent(AppComponentBuilder.getComponent())
                .build()
        return requireNotNull(instance)
    }
}