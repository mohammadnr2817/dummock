package dev.radis.dummock.model.entity.response


import com.google.gson.annotations.SerializedName

data class Leg(
    @SerializedName("distance")
    val distance: Distance,
    @SerializedName("duration")
    val duration: Duration,
    @SerializedName("steps")
    val steps: List<Step>,
    @SerializedName("summary")
    val summary: String
)