package dev.radis.dummock.model.entity

import dev.radis.dummock.utils.constants.StringConstants.DIRECTION_TYPE_CAR

data class DirectionModel(
    var origin: Point? = null,
    var destination: Point? = null,
    val points: MutableList<Point> = ArrayList(),
    var distance: String? = null,
    var duration: String? = null,
    var directionRequestType: String = DIRECTION_TYPE_CAR,
)
