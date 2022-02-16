package dev.radis.dummock.model.repository

import dev.radis.dummock.model.dataSource.DirectionReader
import dev.radis.dummock.model.entity.DirectionRequestModel
import dev.radis.dummock.model.entity.Point
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DirectionRepositoryImpl : DirectionRepository {
    private val directionReader = DirectionReader()
    override suspend fun getDirection(points: List<Point>): List<Point> {
        val res: MutableList<Point> = arrayListOf()

        for (i in points.indices - 1) {
            val directionRequest =
                withContext(Dispatchers.IO) {
                    res.addAll(
                        directionReader.read(
                            DirectionRequestModel(
                                points[i],
                                points[i + 1]
                            )
                        )
                    )
                }
        }
        return res
    }
}