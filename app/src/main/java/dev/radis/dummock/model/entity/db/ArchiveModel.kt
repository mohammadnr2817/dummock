package dev.radis.dummock.model.entity.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import dev.radis.dummock.model.db.DirectionModelTypeConverter
import dev.radis.dummock.model.entity.DirectionModel
import dev.radis.dummock.utils.constants.StringConstants.ARCHIVE_TABLE_NAME

@Entity(tableName = ARCHIVE_TABLE_NAME)
data class ArchiveModel(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var title: String,
    var date: String,
    var speed: Int,
    @TypeConverters(DirectionModelTypeConverter::class)
    var direction: DirectionModel
)