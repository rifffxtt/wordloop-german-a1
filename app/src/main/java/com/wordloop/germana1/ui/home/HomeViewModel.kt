package com.wordloop.germana1.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wordloop.germana1.data.entity.DeckEntity
import com.wordloop.germana1.repository.DeckRepository
import com.wordloop.germana1.repository.StudyLogRepository
import com.wordloop.germana1.util.DateUtils
import com.wordloop.germana1.util.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val deckRepository: DeckRepository,
    private val studyLogRepository: StudyLogRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    val dailyGoal = preferencesManager.getDailyGoal()

    val vocabularyDecks = deckRepository.getDecksByCategory("VOCABULARY")
    val grammarDecks = deckRepository.getDecksByCategory("GRAMMAR")
    val skillsDecks = deckRepository.getDecksByCategory("SKILLS_PRACTICE")

    val allUnlockedDecks = deckRepository.getUnlockedDecks()

    val dailyProgress = studyLogRepository.getRecentLogs().map { logs ->
        val todayLog = logs.find { it.date == DateUtils.today() }
        val reviewed = todayLog?.cardsReviewed ?: 0
        val completed = todayLog?.goalCompleted ?: false
        DailyProgress(reviewed, dailyGoal, completed)
    }

    val streak = studyLogRepository.getCompletedDates().map { dates ->
        DateUtils.calculateStreak(dates)
    }

    val quickPracticeDecks = combine(vocabularyDecks, grammarDecks, skillsDecks) { vocab, grammar, skills ->
        listOf(
            QuickPracticeItem("Mistakes / Hard Cards", "mistakes", vocab + grammar),
            QuickPracticeItem("Article Quiz (Der/Die/Das)", "article_quiz", vocab + grammar),
            QuickPracticeItem("4-Skills Practice", "skills_practice", skills)
        )
    }

    fun refreshDailyGoal() {
        // Daily goal is read from preferences
    }
}

data class DailyProgress(
    val cardsReviewed: Int,
    val dailyGoal: Int,
    val goalCompleted: Boolean
) {
    val progress: Float = if (dailyGoal > 0) cardsReviewed.toFloat() / dailyGoal else 0f
}

data class QuickPracticeItem(
    val title: String,
    val type: String,
    val decks: List<DeckEntity>
)