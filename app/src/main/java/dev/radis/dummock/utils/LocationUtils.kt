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

    fun findBearing(
        point1: Point,
        point2: Point,
    ): Double {
        val longitude1: Double = point1.lng
        val longitude2: Double = point2.lng
        val latitude1 = Math.toRadians(point1.lat)
        val latitude2 = Math.toRadians(point2.lat)
        val longDiff = Math.toRadians(longitude2 - longitude1)
        val y = sin(longDiff) * cos(latitude2)
        val x = cos(latitude1) * sin(latitude2) - sin(latitude1) * cos(latitude2) * cos(longDiff)
        return ((Math.toDegrees(atan2(y, x)) + 360) % 360)
    }

}