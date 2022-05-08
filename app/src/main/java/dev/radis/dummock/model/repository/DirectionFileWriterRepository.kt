package dev.radis.dummock.model.repository

import dev.radis.dummock.model.entity.Point
import dev.radis.dummock.utils.response.Response

interface DirectionFileWriterRepository {
    suspend fun writeFile(
        points: List<Point>
    ): Response<String, Throwable>

    fun dispose()
}