package dev.radis.dummock.model.repository

import dev.radis.dummock.model.dataSource.DirectionReader
import dev.radis.dummock.model.entity.DirectionRequestModel
import dev.radis.dummock.model.entity.Point
import dev.radis.dummock.utils.constants.StringConstants.DEFAULT_DIRECTION_TYPE
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

    override suspend fun getDirection(points: List<Point>): Response<List<Point>, Throwable> {
        val result = arrayListOf<Point>()
        var hasError = false

        for (i in points.indices - 1) {
            coroutineScope.async {
                val directionResponse = directionReader.read(
                    DirectionRequestModel(
                        DEFAULT_DIRECTION_TYPE,
                        points[i],
                        points[i + 1]
                    )
                )
                directionResponse.ifNotSuccessful {
                    hasError = true
                }
                directionResponse.ifSuccessful {
                    result.addAll(it)
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