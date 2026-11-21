package com.wordloop.germana1.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.wordloop.germana1.data.entity.CardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(cards: List<CardEntity>)

    @Update
    suspend fun update(card: CardEntity)

    @Update
    suspend fun updateAll(cards: List<CardEntity>)

    @Query("SELECT * FROM cards WHERE deckId = :deckId ORDER BY nextReviewAt ASC, masteryLevel ASC, RANDOM()")
    fun getCardsForStudy(deckId: Int): Flow<List<CardEntity>>

    @Query("SELECT * FROM cards WHERE deckId = :deckId ORDER BY id ASC")
    fun getAllCardsByDeck(deckId: Int): Flow<List<CardEntity>>

    @Query("SELECT * FROM cards WHERE deckId = :deckId AND masteryLevel = 0 ORDER BY nextReviewAt ASC")
    fun getHardCards(deckId: Int): Flow<List<CardEntity>>

    @Query("SELECT * FROM cards WHERE deckId = :deckId AND masteryLevel = 2")
    fun getMasteredCards(deckId: Int): Flow<List<CardEntity>>

    @Query("SELECT * FROM cards WHERE deckId IN (:deckIds) AND masteryLevel = 0 ORDER BY nextReviewAt ASC LIMIT :limit")
    fun getMistakesAcrossDecks(deckIds: List<Int>, limit: Int): Flow<List<CardEntity>>

    @Query("SELECT * FROM cards WHERE frontText LIKE :query OR meaningEn LIKE :query OR meaningBn LIKE :query ORDER BY id ASC")
    fun searchCards(query: String): Flow<List<CardEntity>>

    @Query("SELECT COUNT(*) FROM cards WHERE deckId = :deckId")
    suspend fun getCardCount(deckId: Int): Int

    @Query("SELECT COUNT(*) FROM cards WHERE deckId = :deckId AND masteryLevel = 2")
    suspend fun getMasteredCount(deckId: Int): Int

    @Query("SELECT * FROM cards WHERE id = :id")
    suspend fun getCardById(id: Int): CardEntity?
}