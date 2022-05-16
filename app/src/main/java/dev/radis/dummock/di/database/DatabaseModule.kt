package dev.radis.dummock.di.database

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dev.radis.dummock.model.db.ArchiveDatabase
import dev.radis.dummock.utils.constants.StringConstants.ARCHIVE_DATABASE_NAME
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(context: Context): ArchiveDatabase {
        return Room.databaseBuilder(context, ArchiveDatabase::class.java, ARCHIVE_DATABASE_NAME)
            // Allows Room to destructively recreate database tables
            // if Migrations that would migrate old database schemas to the latest schema version are not found.
            .fallbackToDestructiveMigration()
            .build()
    }

}