package dev.radis.dummock.model.dataSource

import dev.radis.dummock.model.api.NeshanApiService
import dev.radis.dummock.model.entity.DirectionModel
import dev.radis.dummock.model.entity.DirectionRequestModel
import dev.radis.dummock.utils.PolylineEncodeUtil
import dev.radis.dummock.utils.response.Failure
import dev.radis.dummock.utils.response.Response
import dev.radis.dummock.utils.response.Success
import javax.inject.Inject

class DirectionReader @Inject constructor(
    private val apiService: NeshanApiService
) :
    Reader.IO.Suspend<DirectionRequestModel, Response<DirectionModel, Throwable>> {
    override suspend fun read(input: DirectionRequestModel): Response<DirectionModel, Throwable> {
        try {
            val response = apiService.getDirection(
                input.type,
                input.origin.toRequestString(),
                input.destination.toRequestString(),
            )
            if (!response.isSuccessful) {
                return Failure(Exception("Response exception!"))
            }
            val result = DirectionModel()
            response.body()?.let { body ->
                body.routes.forEach { route ->
                    route.legs.forEach { leg ->
                        result.distance = leg.distance.text
                        result.duration = leg.duration.text
                        leg.steps.forEach { step ->
                            result.points.addAll(PolylineEncodeUtil.decode(step.polyline))
                        }
                    }
                }
                return Success(result)
            } ?: let {
                return Failure(Exception("Response body exception!"))
            }
        } catch (t: Throwable) {
            return Failure(t)
        }
    }
}