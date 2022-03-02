package dev.radis.dummock.di.networking

import dagger.Component
import dev.radis.dummock.model.api.NeshanApiService
import javax.inject.Singleton

@Component(modules = [NetworkingModule::class])
@Singleton
interface NetworkingComponent {

    fun exposeNeshanApiService(): NeshanApiService

}