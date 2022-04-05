package dev.radis.dummock.di.application

import android.app.Application
import android.content.Context
import dagger.Binds
import dagger.Module

@Module
interface AppModule {

    @Binds
    fun bindContext(application: Application): Context

}