package dev.radis.dummock.utils

import dev.radis.dummock.model.entity.Point
import kotlin.math.*

object LocationUtils {
    fun haversine(
        point1: Point,
        point2: Point,
    ): Double {
        // distance between latitudes and longitudes
        var lat1 = point1.lat
        var lat2 = point2.lat
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(point2.lng - point1.lng)

        // convert to radians
        lat1 = Math.toRadians(lat1)
        lat2 = Math.toRadians(lat2)

        // apply formulae
        val a = sin(dLat / 2).pow(2.0) +
                sin(dLon / 2).pow(2.0) *
                cos(lat1) *
                cos(lat2)
        val rad = 6371.0
        val c = 2 * asin(sqrt(a))
        return rad * c // in kilometers
    }
}