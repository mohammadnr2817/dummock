package dev.radis.dummock.model.preference

interface Preferences {
    suspend fun setDefaultSpeed(speed: Int)

    suspend fun getDefaultSpeed(): Int
}