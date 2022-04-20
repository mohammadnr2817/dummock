package dev.radis.dummock.view.state

import android.content.Intent
import dev.radis.dummock.model.entity.DirectionModel
import dev.radis.dummock.model.entity.Point
import dev.radis.dummock.utils.SingleUse
import dev.radis.dummock.utils.constants.NumericConstants
import dev.radis.dummock.utils.constants.StringConstants
import dev.radis.dummock.utils.mvi.MviState

data class MapState(
    val isLoading: SingleUse<Boolean>? = null,
    val message: SingleUse<String>? = null,
    val markers: SingleUse<List<Point>> = SingleUse(emptyList()),
    val direction: SingleUse<DirectionModel>? = null,
    var directionRequestType: String = StringConstants.DIRECTION_TYPE_CAR,
    val speed: Int = NumericConstants.DEFAULT_SPEED_KMH,
    val serviceRunning: SingleUse<Boolean>? = null,
    val executeIntent: SingleUse<Intent>? = null
) : MviState