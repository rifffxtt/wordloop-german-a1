package com.wordloop.germana1.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.wordloop.germana1.data.entity.DeckEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DeckDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(decks: List<DeckEntity>)

    @Update
    suspend fun update(deck: DeckEntity)

    @Query("SELECT * FROM decks WHERE category = :category ORDER BY id ASC")
    fun getDecksByCategory(category: String): Flow<List<DeckEntity>>

    @Query("SELECT * FROM decks WHERE id = :id")
    suspend fun getDeckById(id: Int): DeckEntity?

    @Query("SELECT * FROM decks WHERE isLocked = 0 ORDER BY category, id ASC")
    fun getUnlockedDecks(): Flow<List<DeckEntity>>

    @Query("SELECT * FROM decks ORDER BY category, id ASC")
    fun getAllDecks(): Flow<List<DeckEntity>>

    @Query("UPDATE decks SET isLocked = 0 WHERE id = :id")
    suspend fun unlockDeck(id: Int)
}