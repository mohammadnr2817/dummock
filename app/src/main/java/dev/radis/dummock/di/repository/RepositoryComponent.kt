package dev.radis.dummock.di.repository

import dagger.Component
import dev.radis.dummock.di.networking.NetworkingComponent
import dev.radis.dummock.model.repository.DirectionRepository

@Component(modules = [RepositoryModule::class], dependencies = [NetworkingComponent::class])
@RepositoryScope
interface RepositoryComponent {

    fun exposeDirectionRepository(): DirectionRepository
}