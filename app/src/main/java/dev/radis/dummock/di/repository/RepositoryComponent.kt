package dev.radis.dummock.di.repository

import dagger.Component
import dev.radis.dummock.di.application.AppComponent
import dev.radis.dummock.di.database.DatabaseComponent
import dev.radis.dummock.di.networking.NetworkingComponent
import dev.radis.dummock.model.repository.DatabaseRepository
import dev.radis.dummock.model.repository.DirectionFileRepository
import dev.radis.dummock.model.repository.DirectionRepository

@Component(
    modules = [RepositoryModule::class],
    dependencies = [NetworkingComponent::class, AppComponent::class, DatabaseComponent::class]
)
@RepositoryScope
interface RepositoryComponent {

    fun exposeDirectionRepository(): DirectionRepository

    fun exposeDirectionFileWriterRepository(): DirectionFileRepository

    fun exposeDatabaseRepository(): DatabaseRepository

}