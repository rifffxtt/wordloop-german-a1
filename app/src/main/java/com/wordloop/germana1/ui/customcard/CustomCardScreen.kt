package com.wordloop.germana1.ui.customcard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHostController
import com.wordloop.germana1.data.entity.DeckEntity
import com.wordloop.germana1.repository.DeckRepository
import com.wordloop.germana1.ui.components.*
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.text.input.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material.icons.filled.Close

@Composable
fun CustomCardScreen(
    navController: NavHostController,
    editingCard: com.wordloop.germana1.data.entity.CardEntity? = null
) {
    val viewModel: CustomCardViewModel = hiltViewModel()
    val decks by viewModel.decks.collectAsState(initial = emptyList())

    // Initialize form with editing card if provided
    androidx.compose.runtime.LaunchedEffect(editingCard) {
        if (editingCard != null) {
            viewModel.setEditingCard(editingCard)
        }
    }

    val germanWord by viewModel.germanWord.collectAsState()
    val pronunciationNote by viewModel.pronunciationNote.collectAsState()
    val englishMeaning by viewModel.englishMeaning.collectAsState()
    val bengaliMeaning by viewModel.bengaliMeaning.collectAsState()
    val exampleSentence by viewModel.exampleSentence.collectAsState()
    val grammarTip by viewModel.grammarTip.collectAsState()
    val selectedDeckId by viewModel.selectedDeckId.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            // Top App Bar
            TopAppBar(
                modifier = Modifier.fillMaxWidth(),
                title = { 
                    Text(if (editingCard != null) "Edit Card" else "Add Custom Card", fontWeight = FontWeight.Bold) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )

            // Form
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(androidx.compose.foundation.rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Deck Selection
                if (decks.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { /* Show deck picker */ }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Deck", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                val selectedDeck = decks.find { it.id == selectedDeckId }
                                Text(
                                    selectedDeck?.title ?: "Select a deck",
                                    fontSize = 16.sp,
                                    color = if (selectedDeck != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Icon(androidx.compose.material.icons.filled.ArrowForwardIos, contentDescription = "Select")
                        }
                    }
                }

                // German Word (Required)
                OutlinedTextField(
                    value = germanWord,
                    onValueChange = { viewModel.germanWord.value = it },
                    label = { Text(stringResource(R.string.german_word)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )

                // Pronunciation Note
                OutlinedTextField(
                    value = pronunciationNote,
                    onValueChange = { viewModel.pronunciationNote.value = it },
                    label = { Text(stringResource(R.string.pronunciation_note)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // English Meaning (Required)
                OutlinedTextField(
                    value = englishMeaning,
                    onValueChange = { viewModel.englishMeaning.value = it },
                    label = { Text(stringResource(R.string.english_meaning)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Bengali Meaning (Required)
                OutlinedTextField(
                    value = bengaliMeaning,
                    onValueChange = { viewModel.bengaliMeaning.value = it },
                    label = { Text(stringResource(R.string.bengali_meaning)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Example Sentence
                OutlinedTextField(
                    value = exampleSentence,
                    onValueChange = { viewModel.exampleSentence.value = it },
                    label = { Text(stringResource(R.string.example_sentence_hint)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Grammar Tip
                OutlinedTextField(
                    value = grammarTip,
                    onValueChange = { viewModel.grammarTip.value = it },
                    label = { Text(stringResource(R.string.grammar_tip_hint)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Save Button
                Button(
                    onClick = {
                        viewModel.saveCard {
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    enabled = germanWord.isNotBlank() && englishMeaning.isNotBlank() && bengaliMeaning.isNotBlank() && selectedDeckId != null
                ) {
                    Text(stringResource(R.string.save), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}