package com.wordloop.germana1.ui.study

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotateY
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.px
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHostController
import com.wordloop.germana1.data.entity.CardEntity
import com.wordloop.germana1.tts.TtsManager
import com.wordloop.germana1.ui.components.*
import com.wordloop.germana1.util.DateUtils
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.SlowMotionVideo
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Check
import androidx.compose.ui.platform.LocalContext

@Composable
fun StudyScreen(
    navController: NavHostController,
    deckId: Int,
    ttsManager: TtsManager
) {
    val viewModel: StudyViewModel = hiltViewModel()
    val context = LocalContext.current

    val currentCard by viewModel.currentCard.collectAsState()
    val isFlipped by viewModel.isFlipped.collectAsState()
    val progressText by viewModel.progressText.collectAsState()
    val sessionStats by viewModel.sessionStats.collectAsState()

    // Initialize session on first composition
    androidx.compose.runtime.LaunchedEffect(deckId) {
        viewModel.startSession(deckId)
    }

    val rotationY by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(600, delayMillis = 0, easing = androidx.compose.animation.core.LinearEasing)
    )

    val frontRotation = if (rotationY > 90) 180f else 0f
    val backRotation = if (rotationY > 90) 0f else -180f

    if (currentCard == null) {
        // Session complete or no cards
        SessionCompleteScreen(
            navController = navController,
            summary = viewModel.getSessionSummary(),
            onPracticeMore = { viewModel.startSession(deckId) },
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

            // Flip Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(320.dp),
                contentAlignment = Alignment.Center
            ) {
                // Front of Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                        .graphicsLayer {
                            rotationY = frontRotation
                            cameraDistance = 8000f
                        }
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(16.dp)
                        )
                        .pointerInput(Unit) {
                            detectTapGestures(onTap = { if (!isFlipped) viewModel.flipCard() })
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Article Chip
                        ArticleChip(currentCard.article)
                        
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))
                        
                        // German Word
                        Text(
                            text = currentCard.frontText,
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                        
                        // Pronunciation Note
                        currentCard.pronunciationNote?.let { note ->
                            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
                            Text(
                                text = note,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                                textAlign = TextAlign.Center
                            )
                        }
                        
                        // TTS Buttons
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 24.dp))
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SpeakerButton(
                                onClick = { ttsManager.speak(currentCard.frontText) },
                                slow = false
                            )
                            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(start = 16.dp))
                            SpeakerButton(
                                onClick = { ttsManager.speakSlow(currentCard.frontText) },
                                slow = true
                            )
                        }
                        
                        // Flip Hint
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 24.dp))
                        Text(
                            "Tap to flip",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f)
                        )
                    }
                }

                // Back of Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                        .graphicsLayer {
                            rotationY = backRotation + 180f
                            cameraDistance = 8000f
                        }
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            color = MaterialTheme.colorScheme.surface,
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.TopStart
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // English Meaning
                        MeaningRow(
                            label = stringResource(R.string.meaning_en),
                            value = currentCard.meaningEn,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        // Bengali Meaning
                        MeaningRow(
                            label = stringResource(R.string.meaning_bn),
                            value = currentCard.meaningBn,
                            color = MaterialTheme.colorScheme.getColor("article_die")
                        )
                        
                        // Example Sentence
                        currentCard.exampleSentence?.let { example ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(crossAxisSize = androidx.compose.ui.layout.LayoutWeight(fill = true)) {
                                    Text("Example", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(example, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                                    
                                    currentCard.exampleTranslation?.let { translation ->
                                        Text(translation, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                Column {
                                    SpeakerButton(onClick = { ttsManager.speak(example) })
                                    if (currentCard.exampleTranslation != null) {
                                        SpeakerButton(
                                            onClick = { ttsManager.speak(currentCard.exampleTranslation!!) },
                                            slow = true
                                        )
                                    }
                                }
                            }
                        }
                        
                        // Grammar Tip
                        currentCard.grammarTip?.let { tip ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.secondaryVariant.copy(alpha = 0.1f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = androidx.compose.material.icons.filled.Lightbulb,
                                        contentDescription = "Tip",
                                        tint = MaterialTheme.colorScheme.secondary
                                    )
                                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(start = 8.dp))
                                    Text(
                                        text = "Grammar Tip: $tip",
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Rating Buttons (shown after flip)
            if (isFlipped) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    RatingButton(
                        label = stringResource(R.string.hard),
                        color = MaterialTheme.colorScheme.getColor("mastery_hard"),
                        onClick = { viewModel.rateCard(0) }
                    )
                    RatingButton(
                        label = stringResource(R.string.good),
                        color = MaterialTheme.colorScheme.getColor("mastery_good"),
                        onClick = { viewModel.rateCard(1) }
                    )
                    RatingButton(
                        label = stringResource(R.string.easy),
                        color = MaterialTheme.colorScheme.getColor("mastery_easy"),
                        onClick = { viewModel.rateCard(2) }
                    )
                }
            }
        }
    }
}

@Composable
fun MeaningRow(
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        crossAxisSize = androidx.compose.ui.layout.LayoutWeight(fill = true)
    ) {
        Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontSize = 20.sp, fontWeight = FontWeight.Medium, color = color)
    }
}

@Composable
fun RatingButton(
    label: String,
    color: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.weight(1f).height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(label, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SessionCompleteScreen(
    navController: NavHostController,
    summary: StudyViewModel.SessionSummary,
    onPracticeMore: () -> Unit,
    onBackHome: () -> Unit
) {
    val context = LocalContext.current
    val adManager: AdManager = androidx.hilt.navigation.compose.hiltViewModel() { AdManager.getInstance(LocalContext.current) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Session Complete!", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 24.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                StatRow("Cards Studied", summary.totalCards.toString())
                StatRow("Accuracy", "${summary.accuracy}%")
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))
                Text("Mastery Breakdown", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    MasteryStat("Hard", summary.hardCount, MaterialTheme.colorScheme.getColor("mastery_hard"))
                    MasteryStat("Good", summary.goodCount, MaterialTheme.colorScheme.getColor("mastery_good"))
                    MasteryStat("Easy", summary.easyCount, MaterialTheme.colorScheme.getColor("mastery_easy"))
                }
            }
        }
        
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 24.dp))
        
        Button(
            onClick = onPracticeMore,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Practice More", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 12.dp))
        
        Button(
            onClick = {
                adManager.showInterstitialAdSuspend(context as android.app.Activity)
                onBackHome()
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Text("Back to Home", fontSize = 18.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun MasteryStat(label: String, count: Int, color: androidx.compose.ui.graphics.Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(count.toString(), fontSize = 24.sp, fontWeight = FontWeight.Bold, color = color)
    }
}