package com.haven.app.di

import android.content.Context
import androidx.room.Room
import com.haven.app.data.HavenDatabase
import com.haven.app.data.SeedDatabaseCallback
import com.haven.app.data.dao.CategoryDao
import com.haven.app.data.dao.EntryDao
import com.haven.app.data.dao.EntryTypeDao
import com.haven.app.data.dao.LabelDao
import com.haven.app.data.dao.MeasurementTypeDao
import com.haven.app.data.dao.TagDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): HavenDatabase {
        return Room.databaseBuilder(
            context,
            HavenDatabase::class.java,
            HavenDatabase.NAME
        )
            .addCallback(SeedDatabaseCallback(context))
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideMeasurementTypeDao(db: HavenDatabase): MeasurementTypeDao = db.measurementTypeDao()

    @Provides
    fun provideCategoryDao(db: HavenDatabase): CategoryDao = db.categoryDao()

    @Provides
    fun provideEntryTypeDao(db: HavenDatabase): EntryTypeDao = db.entryTypeDao()

    @Provides
    fun provideLabelDao(db: HavenDatabase): LabelDao = db.labelDao()

    @Provides
    fun provideTagDao(db: HavenDatabase): TagDao = db.tagDao()

    @Provides
    fun provideEntryDao(db: HavenDatabase): EntryDao = db.entryDao()
}
