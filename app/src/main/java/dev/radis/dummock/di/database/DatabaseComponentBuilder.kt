package dev.radis.dummock.di.database

import dev.radis.dummock.di.application.AppComponentBuilder

object DatabaseComponentBuilder {
    private var instance: DatabaseComponent? = null

    fun getComponent(): DatabaseComponent {
        if (instance == null) instance =
            DaggerDatabaseComponent.builder()
                .appComponent(AppComponentBuilder.getComponent())
                .databaseModule(DatabaseModule())
                .build()
        return requireNotNull(instance)
    }
}