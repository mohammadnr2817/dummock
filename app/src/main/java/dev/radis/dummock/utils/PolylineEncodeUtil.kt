package dev.radis.dummock.utils

import dev.radis.dummock.model.entity.Point
import kotlin.math.roundToLong

object PolylineEncodeUtil {
    /**
     * Decodes an encoded path string into a sequence of LatLng.
     */
    fun decode(encodedPath: String): List<Point> {
        val len = encodedPath.length

        // For speed we preallocate to an upper bound on the final length, then
        // truncate the array before returning.
        val path: MutableList<Point> = ArrayList<Point>()
        var index = 0
        var lat = 0
        var lng = 0
        while (index < len) {
            var result = 1
            var shift = 0
            var b: Int
            do {
                b = encodedPath[index++].code - 63 - 1
                result += b shl shift
                shift += 5
            } while (b >= 0x1f)
            lat += if (result and 1 != 0) (result shr 1).inv() else result shr 1
            result = 1
            shift = 0
            do {
                b = encodedPath[index++].code - 63 - 1
                result += b shl shift
                shift += 5
            } while (b >= 0x1f)
            lng += if (result and 1 != 0) (result shr 1).inv() else result shr 1
            path.add(Point(lat * 1e-5, lng * 1e-5))
        }
        return path
    }

    /**
     * Encodes a sequence of LatLng into an encoded path string.
     */
    fun encode(path: List<Point>): String {
        var lastLat: Long = 0
        var lastLng: Long = 0
        val result = StringBuffer()
        for (point in path) {
            val lat = (point.lat!! * 1e5).roundToLong()
            val lng = (point.lng * 1e5).roundToLong()
            val dLat = lat - lastLat
            val dLng = lng - lastLng
            encode(dLat, result)
            encode(dLng, result)
            lastLat = lat
            lastLng = lng
        }
        return result.toString()
    }

    private fun encode(v: Long, result: StringBuffer) {
        var v = v
        v = if (v < 0) (v shl 1).inv() else v shl 1
        while (v >= 0x20) {
            result.append(Character.toChars(((0x20 or (v and 0x1f).toInt()) + 63)))
            v = v shr 5
        }
        result.append(Character.toChars((v + 63).toInt()))
    }
}