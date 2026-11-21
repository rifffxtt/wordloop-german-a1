package com.wordloop.germana1.di

import android.content.Context
import androidx.room.Room
import com.wordloop.germana1.data.SeedDataLoader
import com.wordloop.germana1.data.WordLoopDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@dagger.hilt.android.qualifiers.ApplicationContext context: Context): WordLoopDatabase {
        return Room.databaseBuilder(
            context,
            WordLoopDatabase::class.java,
            "wordloop.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideDeckDao(database: WordLoopDatabase) = database.deckDao()

    @Provides
    @Singleton
    fun provideCardDao(database: WordLoopDatabase) = database.cardDao()

    @Provides
    @Singleton
    fun provideQuizQuestionDao(database: WordLoopDatabase) = database.quizQuestionDao()

    @Provides
    @Singleton
    fun provideStudyLogDao(database: WordLoopDatabase) = database.studyLogDao()

    @Provides
    @Singleton
    fun provideSeedDataLoader(
        @dagger.hilt.android.qualifiers.ApplicationContext context: Context,
        database: WordLoopDatabase,
        preferencesManager: com.wordloop.germana1.util.PreferencesManager
    ): SeedDataLoader {
        return SeedDataLoader(context, database, preferencesManager)
    }
}