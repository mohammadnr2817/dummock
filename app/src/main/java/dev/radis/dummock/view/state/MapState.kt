package dev.radis.dummock.view.state

import dev.radis.dummock.model.entity.DirectionModel
import dev.radis.dummock.model.entity.Point
import dev.radis.dummock.utils.SingleUse
import dev.radis.dummock.utils.mvi.MviState

data class MapState(
    val isLoading: SingleUse<Boolean>? = null,
    val message: SingleUse<String>? = null,
    val markers: SingleUse<List<Point>> = SingleUse(emptyList()),
    val direction: SingleUse<DirectionModel>? = null,
) : MviState