package dev.radis.dummock.di.map

import dagger.Component
import dev.radis.dummock.di.repository.RepositoryComponent
import dev.radis.dummock.di.viewmodel.ViewModelModule
import dev.radis.dummock.view.MapActivity

@Component(
    dependencies = [RepositoryComponent::class],
    modules = [ViewModelModule::class]
)
@ActivityScope
interface MapComponent {

    fun inject(activity: MapActivity)

}