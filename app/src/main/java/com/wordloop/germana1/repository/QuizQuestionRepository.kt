package com.wordloop.germana1.repository

import com.wordloop.germana1.data.dao.QuizQuestionDao
import com.wordloop.germana1.data.entity.QuizQuestionEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuizQuestionRepository @Inject constructor(
    private val quizQuestionDao: QuizQuestionDao
) {
    suspend fun insertAll(questions: List<QuizQuestionEntity>) {
        quizQuestionDao.insertAll(questions)
    }

    fun getQuestionsByType(deckId: Int, type: String, limit: Int): Flow<List<QuizQuestionEntity>> {
        return quizQuestionDao.getQuestionsByType(deckId, type, limit)
    }

    fun getMixedQuestions(deckId: Int, limit: Int): Flow<List<QuizQuestionEntity>> {
        return quizQuestionDao.getMixedQuestions(deckId, limit)
    }

    fun getQuestionsByTypeGlobal(type: String, limit: Int): Flow<List<QuizQuestionEntity>> {
        return quizQuestionDao.getQuestionsByTypeGlobal(type, limit)
    }

    suspend fun getQuestionCount(deckId: Int): Int {
        return quizQuestionDao.getQuestionCount(deckId)
    }
}