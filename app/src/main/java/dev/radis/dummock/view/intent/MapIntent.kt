package dev.radis.dummock.view.intent

import dev.radis.dummock.model.entity.Point
import dev.radis.dummock.utils.mvi.MviIntent

sealed class MapIntent : MviIntent {

    data class LocationSelectedIntent(val point: Point) : MapIntent()

    data class LocationSwitchIntent(
        var firstLocationIndex: Int,
        var secondLocationIndex: Int
    ) : MapIntent()

}