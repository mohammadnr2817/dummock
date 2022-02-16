package dev.radis.dummock.model.repository

import dev.radis.dummock.model.entity.Point

interface DirectionRepository {
    suspend fun getDirection(points: List<Point>): List<Point>
}