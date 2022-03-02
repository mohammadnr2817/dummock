package dev.radis.dummock.di.networking

object NetworkingComponentBuilder {
    private var instance: NetworkingComponent? = null

    fun getComponent(): NetworkingComponent {
        if (instance == null) instance =
            DaggerNetworkingComponent.builder()
                .networkingModule(NetworkingModule())
                .build()
        return requireNotNull(instance)
    }
}