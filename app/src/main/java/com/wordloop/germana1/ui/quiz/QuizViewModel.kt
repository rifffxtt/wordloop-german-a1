package com.wordloop.germana1.ui.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wordloop.germana1.data.entity.CardEntity
import com.wordloop.germana1.data.entity.QuizQuestionEntity
import com.wordloop.germana1.repository.CardRepository
import com.wordloop.germana1.repository.QuizQuestionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val quizQuestionRepository: QuizQuestionRepository,
    private val cardRepository: CardRepository
) : ViewModel() {

    private var currentQuestions: List<QuizQuestionEntity> = emptyList()
    private var currentIndex = 0
    private var correctAnswers = 0
    private var answeredQuestions = 0

    val currentQuestion = MutableStateFlow<QuizQuestionEntity?>(null)
    val selectedOption = MutableStateFlow<String?>(null)
    val showFeedback = MutableStateFlow(false)
    val isCorrect = MutableStateFlow(false)
    val progressText = MutableStateFlow("")
    val quizType = MutableStateFlow<String>("")

    enum class QuizMode {
        ARTICLE_PICKER,
        GRAMMAR_FILL,
        READING_MCQ,
        LISTENING_MATCH,
        MIXED
    }

    fun startQuiz(deckId: Int, mode: QuizMode, questionLimit: Int = 10) {
        currentIndex = 0
        correctAnswers = 0
        answeredQuestions = 0
        quizType.value = mode.name

        viewModelScope.launch {
            val questions = when (mode) {
                QuizMode.ARTICLE_PICKER -> {
                    // Generate article picker questions from cards
                    generateArticlePickerQuestions(deckId, questionLimit)
                }
                QuizMode.GRAMMAR_FILL -> quizQuestionRepository.getQuestionsByType(deckId, "GRAMMAR_FILL", questionLimit)
                QuizMode.READING_MCQ -> quizQuestionRepository.getQuestionsByType(deckId, "READING_MCQ", questionLimit)
                QuizMode.LISTENING_MATCH -> quizQuestionRepository.getQuestionsByType(deckId, "LISTENING_MATCH", questionLimit)
                QuizMode.MIXED -> quizQuestionRepository.getMixedQuestions(deckId, questionLimit)
            }

            questions.collect { qs ->
                currentQuestions = qs
                if (qs.isNotEmpty()) {
                    currentQuestion.value = qs[0]
                    updateProgress()
                }
            }
        }
    }

    private suspend fun generateArticlePickerQuestions(deckId: Int, limit: Int): List<QuizQuestionEntity> {
        val cards = cardRepository.getAllCardsByDeck(deckId).first()
            .filter { it.article != null }
            .shuffled()
            .take(limit)

        return cards.mapIndexed { index, card ->
            val options = listOf("der", "die", "das").shuffled()
            QuizQuestionEntity(
                id = 0,
                deckId = deckId,
                questionType = "ARTICLE_PICKER",
                promptText = "What is the article for: ${card.frontText.replace(card.article!! + " ", "")}?",
                audioText = card.frontText,
                optionA = options[0],
                optionB = options[1],
                optionC = options[2],
                optionD = "",
                correctOption = card.article!!,
                explanationBn = "${card.article} ${card.frontText} - ${card.meaningBn} (${card.meaningEn})"
            )
        }
    }

    fun selectOption(option: String) {
        if (showFeedback.value) return
        selectedOption.value = option
        showFeedback.value = true

        val question = currentQuestion.value ?: return
        val correct = option == question.correctOption
        isCorrect.value = correct

        if (correct) correctAnswers++
        answeredQuestions++
    }

    fun nextQuestion() {
        showFeedback.value = false
        selectedOption.value = null
        currentIndex++

        if (currentIndex < currentQuestions.size) {
            currentQuestion.value = currentQuestions[currentIndex]
            updateProgress()
        } else {
            currentQuestion.value = null
        }
    }

    fun hasNext(): Boolean {
        return currentIndex + 1 < currentQuestions.size
    }

    fun getScore(): Int {
        return if (answeredQuestions > 0) (correctAnswers.toFloat() / answeredQuestions * 100).toInt() else 0
    }

    private fun updateProgress() {
        progressText.value = "${currentIndex + 1} / ${currentQuestions.size}"
    }
}