package dev.radis.dummock.model.repository

import dev.radis.dummock.model.entity.DirectionModel
import dev.radis.dummock.model.entity.Point
import dev.radis.dummock.utils.response.Response

interface DirectionRepository {
    suspend fun getDirection(
        directionRequestType: String,
        points: List<Point>
    ): Response<DirectionModel, Throwable>

    fun dispose()
}