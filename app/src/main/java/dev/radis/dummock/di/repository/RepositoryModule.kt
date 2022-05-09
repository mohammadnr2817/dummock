package dev.radis.dummock.di.repository

import dagger.Binds
import dagger.Module
import dev.radis.dummock.model.repository.DirectionFileRepository
import dev.radis.dummock.model.repository.DirectionFileRepositoryImpl
import dev.radis.dummock.model.repository.DirectionRepository
import dev.radis.dummock.model.repository.DirectionRepositoryImpl

@Module
interface RepositoryModule {

    @Binds
    fun bindDirectionRepository(directionRepositoryImpl: DirectionRepositoryImpl): DirectionRepository

    @Binds
    fun bindDirectionFileWriterRepository(directionFileRepositoryImpl: DirectionFileRepositoryImpl): DirectionFileRepository

}