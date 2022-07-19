package dev.radis.dummock.di.database

import dagger.Component
import dev.radis.dummock.di.application.AppComponent
import dev.radis.dummock.model.db.ArchiveDao
import javax.inject.Singleton

@Component(
    dependencies = [AppComponent::class], modules = [DatabaseModule::class]
)
@Singleton
interface DatabaseComponent {

    fun exposeArchiveDao(): ArchiveDao

}