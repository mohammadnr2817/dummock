package dev.radis.dummock.model.repository

import dev.radis.dummock.model.entity.db.ArchiveModel
import dev.radis.dummock.utils.response.Response

interface DatabaseRepository {

    suspend fun saveArchive(archive: ArchiveModel)

    suspend fun deleteArchive(id: Int)

    suspend fun getArchive(id: Int): Response<ArchiveModel, Throwable>

    suspend fun getArchives(): Response<List<ArchiveModel>, Throwable>
}