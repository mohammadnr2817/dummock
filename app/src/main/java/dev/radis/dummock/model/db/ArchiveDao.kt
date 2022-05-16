package dev.radis.dummock.model.db

import androidx.room.*
import dev.radis.dummock.model.entity.db.ArchiveModel
import dev.radis.dummock.utils.constants.StringConstants

@Dao
interface ArchiveDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addArchive(archive: ArchiveModel)

    @Delete
    suspend fun deleteArchive(archive: ArchiveModel)

    @Query("SELECT * FROM ${StringConstants.ARCHIVE_TABLE_NAME} WHERE id LIKE :id")
    suspend fun getArchive(id: Int): ArchiveModel

    @Query("SELECT * FROM ${StringConstants.ARCHIVE_TABLE_NAME}")
    suspend fun getAllArchives(): List<ArchiveModel>

}