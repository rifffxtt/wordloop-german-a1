package com.wordloop.germana1.ui.study

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wordloop.germana1.data.entity.CardEntity
import com.wordloop.germana1.repository.CardRepository
import com.wordloop.germana1.repository.DeckRepository
import com.wordloop.germana1.repository.StudyLogRepository
import com.wordloop.germana1.util.DateUtils
import com.wordloop.germana1.util.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudyViewModel @Inject constructor(
    private val cardRepository: CardRepository,
    private val deckRepository: DeckRepository,
    private val studyLogRepository: StudyLogRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private var currentDeckId = 0
    private var currentCards: List<CardEntity> = emptyList()
    private var currentIndex = 0

    val currentCard = MutableStateFlow<CardEntity?>(null)
    val isFlipped = MutableStateFlow(false)
    val progressText = MutableStateFlow("")
    val sessionStats = MutableStateFlow(SessionStats(0, 0, 0, 0))

    private var cardsStudied = 0
    private var correctCount = 0
    private var hardCount = 0
    private var goodCount = 0
    private var easyCount = 0

    fun startSession(deckId: Int) {
        currentDeckId = deckId
        currentIndex = 0
        cardsStudied = 0
        correctCount = 0
        hardCount = 0
        goodCount = 0
        easyCount = 0
        isFlipped.value = false

        viewModelScope.launch {
            cardRepository.getCardsForStudy(deckId).collect { cards ->
                currentCards = cards
                if (cards.isNotEmpty()) {
                    currentCard.value = cards[0]
                    updateProgress()
                } else {
                    currentCard.value = null
                }
            }
        }
    }

    fun flipCard() {
        isFlipped.value = true
    }

    fun rateCard(rating: Int) { // 0=Hard, 1=Good, 2=Easy
        val card = currentCard.value ?: return
        cardsStudied++

        when (rating) {
            0 -> {
                hardCount++
                card.masteryLevel = 0
            }
            1 -> {
                goodCount++
                if (card.masteryLevel < 2) card.masteryLevel = 1
            }
            2 -> {
                easyCount++
                card.masteryLevel = 2
            }
        }

        card.reviewCount++
        card.nextReviewAt = System.currentTimeMillis() + DateUtils.addReviewInterval(card.masteryLevel)

        viewModelScope.launch {
            cardRepository.update(card)
            updateStudyLog()
        }

        moveToNextCard()
    }

    private fun moveToNextCard() {
        isFlipped.value = false
        currentIndex++
        if (currentIndex < currentCards.size) {
            currentCard.value = currentCards[currentIndex]
        } else {
            currentCard.value = null
        }
        updateProgress()
    }

    private fun updateProgress() {
        val total = currentCards.size
        val current = min(currentIndex + 1, total)
        progressText.value = "$current / $total"
        sessionStats.value = SessionStats(cardsStudied, hardCount, goodCount, easyCount)
    }

    private fun updateStudyLog() {
        viewModelScope.launch {
            val today = DateUtils.today()
            val log = studyLogRepository.getLogByDate(today) ?: com.wordloop.germana1.data.entity.StudyLogEntity(today, 0, false)
            log.cardsReviewed = cardsStudied
            log.goalCompleted = cardsStudied >= preferencesManager.getDailyGoal()
            studyLogRepository.insert(log)
        }
    }

    fun getSessionSummary(): SessionSummary {
        val total = cardsStudied
        val accuracy = if (total > 0) (correctCount.toFloat() / total * 100).toInt() else 0
        return SessionSummary(
            totalCards = total,
            accuracy = accuracy,
            hardCount = hardCount,
            goodCount = goodCount,
            easyCount = easyCount
        )
    }

    companion object {
        fun min(a: Int, b: Int) = if (a < b) a else b
    }
}

data class SessionStats(
    val studied: Int,
    val hard: Int,
    val good: Int,
    val easy: Int
)

data class SessionSummary(
    val totalCards: Int,
    val accuracy: Int,
    val hardCount: Int,
    val goodCount: Int,
    val easyCount: Int
)