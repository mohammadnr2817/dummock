package dev.radis.dummock.utils.constants

import androidx.annotation.StringDef
import dev.radis.dummock.utils.constants.StringConstants.DIRECTION_TYPE_BIKE
import dev.radis.dummock.utils.constants.StringConstants.DIRECTION_TYPE_CAR

object StringConstants {
    const val BASE_DIRECTION_URL = "https://api.neshan.org/"
    const val DUMMOCK_TAG = "DUMMOCK_TAG"
    const val DIRECTION_TYPE_CAR = "car"
    const val DIRECTION_TYPE_BIKE = "motorcycle"
}

@StringDef(DIRECTION_TYPE_CAR, DIRECTION_TYPE_BIKE)
@Retention(AnnotationRetention.SOURCE)
annotation class DirectionType