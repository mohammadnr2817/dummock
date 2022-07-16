package dev.radis.dummock.model.repository

import dev.radis.dummock.model.db.ArchiveDao
import dev.radis.dummock.model.entity.db.ArchiveModel
import dev.radis.dummock.utils.response.Failure
import dev.radis.dummock.utils.response.Response
import dev.radis.dummock.utils.response.Success
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

class DatabaseRepositoryImpl @Inject constructor(
    private val database: ArchiveDao
) : DatabaseRepository {

    private var coroutineScope = CoroutineScope(Dispatchers.IO)

    override suspend fun saveArchive(archive: ArchiveModel) {
        coroutineScope.launch {
            database.addArchive(archive)
        }
    }

    override suspend fun deleteArchive(archive: ArchiveModel) {
        coroutineScope.launch {
            database.deleteArchive(archive)
        }
    }

    override suspend fun getArchive(id: Int): Response<ArchiveModel, Throwable> {
        var result: ArchiveModel? = null
        coroutineScope.launch {
            result = database.getArchive(id)
        }
        result?.let {
            return Success(it)
        } ?: return Failure(Throwable("Archive not found Exception"))
    }

    override suspend fun getArchives(): Response<List<ArchiveModel>, Throwable> {
        var result: List<ArchiveModel>? = null
        coroutineScope.launch {
            result = database.getAllArchives()
        }
        result?.let {
            return Success(it)
        } ?: return Failure(Throwable("No archive found Exception"))
    }

    override fun dispose() {
        coroutineScope.cancel()
    }
}