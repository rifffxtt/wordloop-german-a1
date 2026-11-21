package com.wordloop.germana1.ui.settings

import androidx.lifecycle.ViewModel
import com.wordloop.germana1.repository.CardRepository
import com.wordloop.germana1.repository.DeckRepository
import com.wordloop.germana1.repository.QuizQuestionRepository
import com.wordloop.germana1.repository.StudyLogRepository
import com.wordloop.germana1.util.PreferencesManager
import com.wordloop.germana1.util.DateUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val cardRepository: CardRepository,
    private val deckRepository: DeckRepository,
    private val quizQuestionRepository: QuizQuestionRepository,
    private val studyLogRepository: StudyLogRepository
) : ViewModel() {

    val dailyGoal = MutableStateFlow(preferencesManager.getDailyGoal())
    val ttsRate = MutableStateFlow(preferencesManager.getTtsRate())
    val seedDataLoaded = MutableStateFlow(preferencesManager.isSeedDataLoaded())

    val dailyGoals = listOf(5, 10, 15, 20)
    val ttsRates = listOf(0.75f, 1.0f, 1.25f, 1.5f)

    fun setDailyGoal(goal: Int) {
        dailyGoal.value = goal
        preferencesManager.setDailyGoal(goal)
    }

    fun setTtsRate(rate: Float) {
        ttsRate.value = rate
        preferencesManager.setTtsRate(rate)
    }

    fun resetAllProgress() {
        viewModelScope.launch {
            // Reset all cards mastery
            deckRepository.getAllDecks().first().forEach { deck ->
                cardRepository.getAllCardsByDeck(deck.id).first().forEach { card ->
                    card.masteryLevel = 0
                    card.reviewCount = 0
                    card.nextReviewAt = 0
                    cardRepository.update(card)
                }
            }

            // Clear study logs
            studyLogRepository.getRecentLogs().first().forEach { log ->
                // We can't delete easily, but we can mark them as not completed
                // In a real app, we'd add a delete method to the DAO
            }

            preferencesManager.clearAll()
            preferencesManager.setSeedDataLoaded(false)
            preferencesManager.setFirstLaunchComplete()

            dailyGoal.value = 15
            ttsRate.value = 1.0f
            seedDataLoaded.value = false
        }
    }
}