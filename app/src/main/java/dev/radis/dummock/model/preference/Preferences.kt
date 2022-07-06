package dev.radis.dummock.model.preference

interface Preferences {
    fun setDefaultSpeed(speed: Int)

    fun getDefaultSpeed(): Int
}