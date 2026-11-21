package com.wordloop.germana1.repository

import com.wordloop.germana1.data.dao.CardDao
import com.wordloop.germana1.data.entity.CardEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CardRepository @Inject constructor(
    private val cardDao: CardDao
) {
    suspend fun insertAll(cards: List<CardEntity>) {
        cardDao.insertAll(cards)
    }

    suspend fun update(card: CardEntity) {
        cardDao.update(card)
    }

    suspend fun updateAll(cards: List<CardEntity>) {
        cardDao.updateAll(cards)
    }

    fun getCardsForStudy(deckId: Int): Flow<List<CardEntity>> {
        return cardDao.getCardsForStudy(deckId)
    }

    fun getAllCardsByDeck(deckId: Int): Flow<List<CardEntity>> {
        return cardDao.getAllCardsByDeck(deckId)
    }

    fun getHardCards(deckId: Int): Flow<List<CardEntity>> {
        return cardDao.getHardCards(deckId)
    }

    fun getMistakesAcrossDecks(deckIds: List<Int>, limit: Int): Flow<List<CardEntity>> {
        return cardDao.getMistakesAcrossDecks(deckIds, limit)
    }

    fun searchCards(query: String): Flow<List<CardEntity>> {
        val searchQuery = "%$query%"
        return cardDao.searchCards(searchQuery)
    }

    suspend fun getCardCount(deckId: Int): Int {
        return cardDao.getCardCount(deckId)
    }

    suspend fun getMasteredCount(deckId: Int): Int {
        return cardDao.getMasteredCount(deckId)
    }

    suspend fun getCardById(id: Int): CardEntity? {
        return cardDao.getCardById(id)
    }
}