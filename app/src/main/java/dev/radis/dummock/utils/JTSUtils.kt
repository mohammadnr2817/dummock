package dev.radis.dummock.utils

import dev.radis.dummock.model.entity.Point
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.linearref.LengthIndexedLine

object JTSUtils {

    fun convertPointsToLengthIndexedLine(points: List<Point>): LengthIndexedLine {
        val factory = GeometryFactory()
        return LengthIndexedLine(
            factory.createLineString(
                points.map { point -> Coordinate(point.lng, point.lat) }.toTypedArray()
            )
        )
    }

}