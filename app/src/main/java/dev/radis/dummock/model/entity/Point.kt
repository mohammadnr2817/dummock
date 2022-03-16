package dev.radis.dummock.model.entity

data class Point(val lat: Double? = null, val lng: Double? = null) {

    fun toRequestString(): String {
        return "$lat,$lng"
    }

    override fun toString(): String {
        return String.format("%.2f,%.2f", lat, lng)
    }

    fun fromRawString(input: String): Point {
        val splitString = input.split(",")
        return Point(splitString[0].toDouble(), splitString[1].toDouble())
    }

}
