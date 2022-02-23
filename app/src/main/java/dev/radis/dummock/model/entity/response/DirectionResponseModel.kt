package dev.radis.dummock.model.entity.response


import com.google.gson.annotations.SerializedName

data class DirectionResponseModel(
    @SerializedName("routes")
    val routes: List<Route>
)