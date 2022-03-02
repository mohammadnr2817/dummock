package dev.radis.dummock.model.repository

import dev.radis.dummock.model.entity.Point
import dev.radis.dummock.utils.response.Response

interface DirectionRepository {
    suspend fun getDirection(points: List<Point>): Response<List<Point>, Throwable>
    fun dispose()
}