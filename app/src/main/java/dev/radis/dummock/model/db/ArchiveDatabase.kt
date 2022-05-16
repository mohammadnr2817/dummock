package dev.radis.dummock.model.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.radis.dummock.model.entity.db.ArchiveModel

@Database(entities = [ArchiveModel::class], version = 1)
@TypeConverters(DirectionModelTypeConverter::class)
abstract class ArchiveDatabase : RoomDatabase() {

    abstract fun dao(): ArchiveDao

}