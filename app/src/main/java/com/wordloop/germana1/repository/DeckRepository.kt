package com.wordloop.germana1.repository

import com.wordloop.germana1.data.dao.DeckDao
import com.wordloop.germana1.data.entity.DeckEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeckRepository @Inject constructor(
    private val deckDao: DeckDao
) {
    fun getDecksByCategory(category: String): Flow<List<DeckEntity>> {
        return deckDao.getDecksByCategory(category)
    }

    suspend fun getDeckById(id: Int): DeckEntity? {
        return deckDao.getDeckById(id)
    }

    fun getUnlockedDecks(): Flow<List<DeckEntity>> {
        return deckDao.getUnlockedDecks()
    }

    fun getAllDecks(): Flow<List<DeckEntity>> {
        return deckDao.getAllDecks()
    }

    suspend fun unlockDeck(id: Int) {
        deckDao.unlockDeck(id)
    }
}