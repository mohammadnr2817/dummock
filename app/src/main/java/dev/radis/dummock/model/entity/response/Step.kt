package dev.radis.dummock.model.entity.response


import com.google.gson.annotations.SerializedName

data class Step(
    @SerializedName("distance")
    val distance: DistanceX,
    @SerializedName("duration")
    val duration: DurationX,
    @SerializedName("instruction")
    val instruction: String,
    @SerializedName("maneuver")
    val maneuver: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("polyline")
    val polyline: String,
    @SerializedName("start_location")
    val startLocation: List<Double>
)