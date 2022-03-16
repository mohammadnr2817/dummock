package dev.radis.dummock.model.entity

data class Point(val lat: Double, val lng: Double) {

    fun toRequestString(): String {
        return "$lat,$lng"
    }

    override fun toString(): String {
        return String.format("%.2f,%.2f", lat, lng)
    }

}
