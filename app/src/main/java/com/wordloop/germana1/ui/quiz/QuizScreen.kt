package com.wordloop.germana1.ui.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHostController
import com.wordloop.germana1.tts.TtsManager
import com.wordloop.germana1.ui.components.*
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.ui.platform.LocalContext

@Composable
fun QuizScreen(
    navController: NavHostController,
    deckId: Int,
    quizMode: QuizViewModel.QuizMode,
    ttsManager: TtsManager
) {
    val viewModel: QuizViewModel = hiltViewModel()
    val context = LocalContext.current

    val currentQuestion by viewModel.currentQuestion.collectAsState()
    val selectedOption by viewModel.selectedOption.collectAsState()
    val showFeedback by viewModel.showFeedback.collectAsState()
    val isCorrect by viewModel.isCorrect.collectAsState()
    val progressText by viewModel.progressText.collectAsState()
    val quizType by viewModel.quizType.collectAsState()

    androidx.compose.runtime.LaunchedEffect(deckId) {
        viewModel.startQuiz(deckId, quizMode)
    }

    if (currentQuestion == null) {
        // Quiz complete
        QuizCompleteScreen(
            navController = navController,
            score = viewModel.getScore(),
            totalQuestions = viewModel.answeredQuestions,
            onRetry = { viewModel.startQuiz(deckId, quizMode) },
            onBackHome = { navController.popBackStack() }
        )
        return
    }

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
                title = { Text(progressText, fontWeight = FontWeight.Medium) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )

            // Question Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor = if (showFeedback && isCorrect) MaterialTheme.colorScheme.getColor("mastery_easy").copy(alpha = 0.1f)
                    else if (showFeedback && !isCorrect) MaterialTheme.colorScheme.getColor("mastery_hard").copy(alpha = 0.1f)
                    else MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Question Type Badge
                    Text(
                        quizType,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    // Audio button for listening questions
                    currentQuestion.audioText?.let { audioText ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            SpeakerButton(onClick = { ttsManager.speak(audioText) })
                            SpeakerButton(onClick = { ttsManager.speakSlow(audioText) }, slow = true)
                        }
                    }
                    
                    // Prompt Text
                    Text(
                        text = currentQuestion.promptText,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    // Options
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OptionButton("A", currentQuestion.optionA, selectedOption, showFeedback, currentQuestion.correctOption) {
                            if (!showFeedback) viewModel.selectOption("A")
                        }
                        OptionButton("B", currentQuestion.optionB, selectedOption, showFeedback, currentQuestion.correctOption) {
                            if (!showFeedback) viewModel.selectOption("B")
                        }
                        OptionButton("C", currentQuestion.optionC, selectedOption, showFeedback, currentQuestion.correctOption) {
                            if (!showFeedback) viewModel.selectOption("C")
                        }
                        if (currentQuestion.optionD.isNotEmpty()) {
                            OptionButton("D", currentQuestion.optionD, selectedOption, showFeedback, currentQuestion.correctOption) {
                                if (!showFeedback) viewModel.selectOption("D")
                            }
                        }
                    }
                }
            }

            // Feedback Section
            if (showFeedback) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCorrect) MaterialTheme.colorScheme.getColor("mastery_easy").copy(alpha = 0.1f)
                        else MaterialTheme.colorScheme.getColor("mastery_hard").copy(alpha = 0.1f)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = if (isCorrect) CheckCircle else Cancel,
                            contentDescription = if (isCorrect) "Correct" : "Incorrect",
                            tint = if (isCorrect) MaterialTheme.colorScheme.getColor("mastery_easy") else MaterialTheme.colorScheme.getColor("mastery_hard"),
                            size = 48.dp
                        )
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
                        Text(
                            text = if (isCorrect) stringResource(R.string.correct) else stringResource(R.string.incorrect),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCorrect) MaterialTheme.colorScheme.getColor("mastery_easy") else MaterialTheme.colorScheme.getColor("mastery_hard")
                        )
                        
                        // Explanation
                        currentQuestion.explanationBn?.let { explanation ->
                            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 12.dp))
                            Text(
                                text = explanation,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                        }
                    }
                }
            }

            // Next Button
            Button(
                onClick = { viewModel.nextQuestion() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                enabled = showFeedback
            ) {
                Text(
                    text = if (viewModel.hasNext()) stringResource(R.string.next_question) else "Finish",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun OptionButton(
    letter: String,
    text: String,
    selectedOption: String?,
    showFeedback: Boolean,
    correctOption: String,
    onClick: () -> Unit
) {
    val isSelected = selectedOption == letter
    val isCorrect = correctOption == letter

    val backgroundColor = when {
        showFeedback && isCorrect -> MaterialTheme.colorScheme.getColor("mastery_easy")
        showFeedback && isSelected && !isCorrect -> MaterialTheme.colorScheme.getColor("mastery_hard")
        isSelected -> MaterialTheme.colorScheme.primaryVariant
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val contentColor = when {
        showFeedback && (isCorrect || isSelected) -> MaterialTheme.colorScheme.onPrimary
        else -> MaterialTheme.colorScheme.onSurface
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .pointerInput(Unit) {
                if (!showFeedback) {
                    androidx.compose.foundation.gestures.detectTapGestures(onTap = onClick)
                }
            }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$letter. ",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
            Text(
                text = text,
                fontSize = 18.sp,
                color = contentColor,
                modifier = Modifier.weight(1f)
            )
            if (showFeedback && isCorrect) {
                Icon(CheckCircle, contentDescription = "Correct", tint = contentColor)
            } else if (showFeedback && isSelected && !isCorrect) {
                Icon(Cancel, contentDescription = "Incorrect", tint = contentColor)
            }
        }
    }
}

@Composable
fun QuizCompleteScreen(
    navController: NavHostController,
    score: Int,
    totalQuestions: Int,
    onRetry: () -> Unit,
    onBackHome: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Quiz Complete!", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))
        
        Text("$score%", fontSize = 72.sp, fontWeight = FontWeight.Bold, 
             color = if (score >= 80) MaterialTheme.colorScheme.getColor("mastery_easy")
             else if (score >= 50) MaterialTheme.colorScheme.getColor("mastery_good")
             else MaterialTheme.colorScheme.getColor("mastery_hard"))
        
        Text("$totalQuestions questions", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 32.dp))
        
        Button(
            onClick = onRetry,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Try Again", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 12.dp))
        
        Button(
            onClick = onBackHome,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Text("Back to Home", fontSize = 18.sp, fontWeight = FontWeight.Medium)
        }
    }
}