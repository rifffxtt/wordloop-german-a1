package com.wordloop.germana1.di

import com.wordloop.germana1.repository.CardRepository
import com.wordloop.germana1.repository.DeckRepository
import com.wordloop.germana1.repository.QuizQuestionRepository
import com.wordloop.germana1.repository.StudyLogRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideDeckRepository(deckDao: com.wordloop.germana1.data.dao.DeckDao): DeckRepository {
        return DeckRepository(deckDao)
    }

    @Provides
    @Singleton
    fun provideCardRepository(cardDao: com.wordloop.germana1.data.dao.CardDao): CardRepository {
        return CardRepository(cardDao)
    }

    @Provides
    @Singleton
    fun provideQuizQuestionRepository(quizQuestionDao: com.wordloop.germana1.data.dao.QuizQuestionDao): QuizQuestionRepository {
        return QuizQuestionRepository(quizQuestionDao)
    }

    @Provides
    @Singleton
    fun provideStudyLogRepository(studyLogDao: com.wordloop.germana1.data.dao.StudyLogDao): StudyLogRepository {
        return StudyLogRepository(studyLogDao)
    }
}