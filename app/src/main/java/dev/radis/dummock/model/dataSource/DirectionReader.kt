package dev.radis.dummock.model.dataSource

import dev.radis.dummock.model.api.NeshanApiService
import dev.radis.dummock.model.entity.DirectionRequestModel
import dev.radis.dummock.model.entity.Point
import dev.radis.dummock.utils.PolylineEncodeUtil
import dev.radis.dummock.utils.response.Failure
import dev.radis.dummock.utils.response.Response
import dev.radis.dummock.utils.response.Success
import javax.inject.Inject

class DirectionReader @Inject constructor(
    val apiService: NeshanApiService
) :
    Reader.IO.Suspend<DirectionRequestModel, Response<List<Point>, Throwable>> {
    override suspend fun read(input: DirectionRequestModel): Response<List<Point>, Throwable> {
        try {
            val response = apiService.getDirection(
                input.type,
                "${input.origin.lat},${input.origin.lng}",
                "${input.destination.lat},${input.destination.lng}"
            )
            if (!response.isSuccessful) {
                return Failure(Throwable(""))
            }
            val points = arrayListOf<Point>()
            response.body()?.let { body ->
                body.routes.forEach { route ->
                    route.legs.forEach { leg ->
                        leg.steps.forEach { step ->
                            points.addAll(PolylineEncodeUtil.decode(step.polyline))
                        }
                    }
                }
                return Success(points)
            } ?: let {
                return Failure(Exception(""))
            }
        } catch (t: Throwable) {
            return Failure(t)
        }
    }
}