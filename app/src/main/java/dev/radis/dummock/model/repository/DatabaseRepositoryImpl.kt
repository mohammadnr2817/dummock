package dev.radis.dummock.model.repository

import dev.radis.dummock.model.db.ArchiveDao
import dev.radis.dummock.model.entity.db.ArchiveModel
import dev.radis.dummock.utils.response.Failure
import dev.radis.dummock.utils.response.Response
import dev.radis.dummock.utils.response.Success
import javax.inject.Inject

class DatabaseRepositoryImpl @Inject constructor(
    private val database: ArchiveDao
) : DatabaseRepository {

    override suspend fun saveArchive(archive: ArchiveModel) {
        database.addArchive(archive)

    }

    override suspend fun deleteArchive(id: Int) {
        database.deleteArchive(id)
    }

    override suspend fun getArchive(id: Int): Response<ArchiveModel, Throwable> {
        val result = database.getArchive(id)
        result?.let {
            return Success(it)
        } ?: return Failure(Throwable("Archive not found Exception"))
    }

    override suspend fun getArchives(): Response<List<ArchiveModel>, Throwable> {
        val result = database.getAllArchives()
        result?.let {
            return Success(it)
        } ?: return Failure(Throwable("No archive found Exception"))
    }
}