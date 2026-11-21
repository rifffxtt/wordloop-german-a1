package com.wordloop.germana1.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wordloop.germana1.data.entity.QuizQuestionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizQuestionDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(questions: List<QuizQuestionEntity>)

    @Query("SELECT * FROM quiz_questions WHERE deckId = :deckId AND questionType = :type ORDER BY RANDOM() LIMIT :limit")
    fun getQuestionsByType(deckId: Int, type: String, limit: Int): Flow<List<QuizQuestionEntity>>

    @Query("SELECT * FROM quiz_questions WHERE deckId = :deckId ORDER BY RANDOM() LIMIT :limit")
    fun getMixedQuestions(deckId: Int, limit: Int): Flow<List<QuizQuestionEntity>>

    @Query("SELECT * FROM quiz_questions WHERE questionType = :type ORDER BY RANDOM() LIMIT :limit")
    fun getQuestionsByTypeGlobal(type: String, limit: Int): Flow<List<QuizQuestionEntity>>

    @Query("SELECT COUNT(*) FROM quiz_questions WHERE deckId = :deckId")
    suspend fun getQuestionCount(deckId: Int): Int
}