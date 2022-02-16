package dev.radis.dummock.model.dataSource

import dev.radis.dummock.model.entity.DirectionRequestModel
import dev.radis.dummock.model.entity.Point

class DirectionReader:Reader.IO<DirectionRequestModel,List<Point>> {
    override fun read(input: DirectionRequestModel): List<Point> {
        TODO("Not yet implemented")
    }
}