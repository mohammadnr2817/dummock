package dev.radis.dummock.model.entity.response


import com.google.gson.annotations.SerializedName

data class DurationX(
    @SerializedName("text")
    val text: String,
    @SerializedName("value")
    val value: Double
)