package com.wordloop.germana1.ui.customcard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wordloop.germana1.data.entity.CardEntity
import com.wordloop.germana1.data.entity.DeckEntity
import com.wordloop.germana1.repository.CardRepository
import com.wordloop.germana1.repository.DeckRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomCardViewModel @Inject constructor(
    private val deckRepository: DeckRepository,
    private val cardRepository: CardRepository
) : ViewModel() {

    val decks = deckRepository.getUnlockedDecks()
    val selectedDeckId = MutableStateFlow<Int?>(null)
    val germanWord = MutableStateFlow("")
    val pronunciationNote = MutableStateFlow("")
    val englishMeaning = MutableStateFlow("")
    val bengaliMeaning = MutableStateFlow("")
    val exampleSentence = MutableStateFlow("")
    val grammarTip = MutableStateFlow("")

    private var editingCard: CardEntity? = null

    fun setEditingCard(card: CardEntity) {
        editingCard = card
        selectedDeckId.value = card.deckId
        germanWord.value = card.frontText
        pronunciationNote.value = card.pronunciationNote ?: ""
        englishMeaning.value = card.meaningEn
        bengaliMeaning.value = card.meaningBn
        exampleSentence.value = card.exampleSentence ?: ""
        grammarTip.value = card.grammarTip ?: ""
    }

    fun clearForm() {
        editingCard = null
        selectedDeckId.value = null
        germanWord.value = ""
        pronunciationNote.value = ""
        englishMeaning.value = ""
        bengaliMeaning.value = ""
        exampleSentence.value = ""
        grammarTip.value = ""
    }

    fun saveCard(onSuccess: () -> Unit) {
        val deckId = selectedDeckId.value ?: return
        val frontText = germanWord.value.trim()
        val meaningEn = englishMeaning.value.trim()
        val meaningBn = bengaliMeaning.value.trim()

        if (frontText.isEmpty() || meaningEn.isEmpty() || meaningBn.isEmpty()) return

        // Extract article from front text if present
        val article = when {
            frontText.startsWith("der ") -> "der"
            frontText.startsWith("die ") -> "die"
            frontText.startsWith("das ") -> "das"
            else -> null
        }

        viewModelScope.launch {
            if (editingCard != null) {
                val card = editingCard!!.copy(
                    deckId = deckId,
                    frontText = frontText,
                    article = article,
                    pronunciationNote = pronunciationNote.value.trim().takeIf { it.isNotEmpty() },
                    meaningEn = meaningEn,
                    meaningBn = meaningBn,
                    exampleSentence = exampleSentence.value.trim().takeIf { it.isNotEmpty() },
                    grammarTip = grammarTip.value.trim().takeIf { it.isNotEmpty() }
                )
                cardRepository.update(card)
            } else {
                val card = CardEntity(
                    id = 0,
                    deckId = deckId,
                    frontText = frontText,
                    article = article,
                    pronunciationNote = pronunciationNote.value.trim().takeIf { it.isNotEmpty() },
                    meaningEn = meaningEn,
                    meaningBn = meaningBn,
                    exampleSentence = exampleSentence.value.trim().takeIf { it.isNotEmpty() },
                    exampleTranslation = null,
                    grammarTip = grammarTip.value.trim().takeIf { it.isNotEmpty() }
                )
                cardRepository.insertAll(listOf(card))
            }
            onSuccess()
        }
    }
}