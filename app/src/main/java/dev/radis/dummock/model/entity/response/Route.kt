package dev.radis.dummock.model.entity.response


import com.google.gson.annotations.SerializedName

data class Route(
    @SerializedName("legs")
    val legs: List<Leg>,
    @SerializedName("overview_polyline")
    val overviewPolyline: OverviewPolyline
)