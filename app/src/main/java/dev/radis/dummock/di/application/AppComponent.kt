package dev.radis.dummock.di.application

import android.app.Application
import android.content.Context
import dagger.BindsInstance
import dagger.Component

@Component(
    modules = [AppModule::class]
)
interface AppComponent {

    fun exposeContext(): Context

    @Component.Builder
    interface Builder {
        fun build(): AppComponent

        @BindsInstance
        fun setApplication(application: Application): Builder
    }
}