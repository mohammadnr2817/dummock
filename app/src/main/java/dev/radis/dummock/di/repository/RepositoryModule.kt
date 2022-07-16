package dev.radis.dummock.di.repository

import dagger.Binds
import dagger.Module
import dev.radis.dummock.model.repository.*

@Module
interface RepositoryModule {

    @Binds
    fun bindDirectionRepository(directionRepositoryImpl: DirectionRepositoryImpl): DirectionRepository

    @Binds
    fun bindDirectionFileWriterRepository(directionFileRepositoryImpl: DirectionFileRepositoryImpl): DirectionFileRepository

    @Binds
    fun bindDatabaseRepository(databaseRepositoryImpl: DatabaseRepositoryImpl): DatabaseRepository

}