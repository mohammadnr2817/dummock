package dev.radis.dummock.model.entity

data class DirectionModel(
    var origin: Point? = null,
    var destination: Point? = null,
    val points: MutableList<Point> = ArrayList(),
    var distance: String? = null,
    var duration: String? = null,
)
