package dev.radis.dummock.di.repository

import dagger.Binds
import dagger.Module
import dev.radis.dummock.model.repository.DirectionRepository
import dev.radis.dummock.model.repository.DirectionRepositoryImpl

@Module
interface RepositoryModule {

    @Binds
    fun bindDirectionRepository(directionRepositoryImpl: DirectionRepositoryImpl): DirectionRepository

}