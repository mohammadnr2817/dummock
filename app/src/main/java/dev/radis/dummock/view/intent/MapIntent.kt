package dev.radis.dummock.view.intent

import dev.radis.dummock.model.entity.Point
import dev.radis.dummock.utils.constants.DirectionType
import dev.radis.dummock.utils.mvi.MviIntent

sealed class MapIntent : MviIntent {

    data class LocationSelectedIntent(val point: Point) : MapIntent()

    object RemoveLastLocationIntent : MapIntent()

    data class LocationSwitchIntent(
        var firstLocationIndex: Int,
        var secondLocationIndex: Int
    ) : MapIntent()

    data class SwitchDirectionTypeIntent(@DirectionType val directionRequestType: String) :
        MapIntent()

    data class CopyToClipboardIntent(val value: String) : MapIntent()

    data class ChangeProviderServiceStateIntent(val value: Boolean) : MapIntent()

    object NavigateInAnotherAppIntent : MapIntent()

}