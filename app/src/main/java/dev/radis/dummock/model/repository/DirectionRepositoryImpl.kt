package dev.radis.dummock.model.repository

import dev.radis.dummock.model.dataSource.DirectionReader
import dev.radis.dummock.model.entity.DirectionRequestModel
import dev.radis.dummock.model.entity.Point
import dev.radis.dummock.utils.constants.StringConstants.DEFAULT_TYPE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DirectionRepositoryImpl @Inject constructor(
    val directionReader: DirectionReader
) :
    DirectionRepository {
    override suspend fun getDirection(points: List<Point>): List<Point> {
        val res: MutableList<Point> = arrayListOf()

        for (i in points.indices - 1) {
            val directionRequest =
                withContext(Dispatchers.IO) {

                    directionReader.read(
                        DirectionRequestModel(
                            DEFAULT_TYPE,
                            points[i],
                            points[i + 1]
                        )
                    ).ifSuccessful { }

                }
        }
        return res
    }
}