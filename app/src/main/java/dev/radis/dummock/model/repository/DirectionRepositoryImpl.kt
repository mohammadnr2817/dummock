package dev.radis.dummock.model.repository

import android.util.Log
import dev.radis.dummock.model.dataSource.DirectionReader
import dev.radis.dummock.model.entity.DirectionModel
import dev.radis.dummock.model.entity.DirectionRequestModel
import dev.radis.dummock.model.entity.Point
import dev.radis.dummock.utils.constants.DirectionType
import dev.radis.dummock.utils.constants.StringConstants.DUMMOCK_TAG
import dev.radis.dummock.utils.response.Failure
import dev.radis.dummock.utils.response.Response
import dev.radis.dummock.utils.response.Success
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import javax.inject.Inject

class DirectionRepositoryImpl @Inject constructor(
    val directionReader: DirectionReader
) : DirectionRepository {

    private var coroutineScope = CoroutineScope(Dispatchers.IO)

    override suspend fun getDirection(
        @DirectionType directionRequestType: String,
        points: List<Point>
    ): Response<DirectionModel, Throwable> {
        val result = DirectionModel()
        var hasError = false

        for (i in points.indices - 1) {
            coroutineScope.async {
                val directionResponse = directionReader.read(
                    DirectionRequestModel(
                        directionRequestType,
                        points[i],
                        points[i + 1]
                    )
                )
                directionResponse.ifNotSuccessful {
                    Log.d(DUMMOCK_TAG, it.toString())
                    hasError = true
                }
                directionResponse.ifSuccessful {
                    result.origin = points.first()
                    result.destination = points.last()
                    result.points.addAll(it.points)
                    result.distance = it.distance
                    result.duration = it.duration
                }
            }.await()
            if (hasError) return Failure(Throwable("Path exception in route ${points[i]} & ${points[i + 1]}"))
        }
        return Success(result)
    }

    override fun dispose() {
        coroutineScope.cancel()
    }
}