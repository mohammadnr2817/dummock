package dev.radis.dummock.model.entity

import org.locationtech.jts.geom.Coordinate

data class Point(val lat: Double, val lng: Double) {

    fun toRequestString(): String {
        return "$lat,$lng"
    }

    override fun toString(): String {
        return String.format("%.4f, %.4f", lat, lng)
    }

    /*
    Coordinate class: X as longitude, Y as latitude and Z is used to keep the elevation data.
     */
    companion object {
        fun fromCoordinate(coordinate: Coordinate) =
            Point(coordinate.y, coordinate.x)
    }

}
