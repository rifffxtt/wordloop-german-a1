package com.wordloop.germana1.ui.quiz

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHostController
import com.wordloop.germana1.data.entity.DeckEntity
import com.wordloop.germana1.repository.DeckRepository
import com.wordloop.germana1.tts.TtsManager
import com.wordloop.germana1.ui.components.*
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SkillsPracticeScreen(
    navController: NavHostController,
    ttsManager: TtsManager
) {
    val viewModel: SkillsPracticeViewModel = hiltViewModel()
    val skillsDecks by viewModel.skillsDecks.collectAsState(initial = emptyList())

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
                title = { Text("4-Skills Practice", fontWeight = FontWeight.Bold) },
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

            // Skills Grid
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SkillCard(
                    title = "Listening Mode",
                    subtitle = "TTS audio → Select meaning",
                    icon = Headphones,
                    color = MaterialTheme.colorScheme.primary,
                    onClick = { navController.navigate("quiz/listening") }
                )
                
                SkillCard(
                    title = "Reading Mini-Passages",
                    subtitle = "A1 dialogues with MCQs",
                    icon = MenuBook,
                    color = MaterialTheme.colorScheme.getColor("article_die"),
                    onClick = { navController.navigate("quiz/reading") }
                )
                
                SkillCard(
                    title = "Writing Builder",
                    subtitle = "Fill-in-the-blank & word ordering",
                    icon = Edit,
                    color = MaterialTheme.colorScheme.getColor("article_das"),
                    onClick = { navController.navigate("quiz/writing") }
                )
                
                SkillCard(
                    title = "Speaking Prompts",
                    subtitle = "Goethe/TELC prompts with TTS",
                    icon = Mic,
                    color = MaterialTheme.colorScheme.secondary,
                    onClick = { navController.navigate("quiz/speaking") }
                )

                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))
                
                SectionHeader("Practice Decks")
                
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(skillsDecks) { deck ->
                        DeckCard(deck = deck, onClick = { 
                            navController.navigate("quiz/mixed/${deck.id}") 
                        })
                    }
                }
            }
        }
    }
}

@Composable
fun SkillCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(56.dp)
                    .background(color.copy(alpha = 0.15f), androidx.compose.foundation.shape.CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, size = 28.dp)
            }
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(start = 16.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text(subtitle, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Icon(ArrowForwardIos, contentDescription = "Navigate", tint = color)
        }
    }
}

@HiltViewModel
class SkillsPracticeViewModel @Inject constructor(
    private val deckRepository: DeckRepository
) : androidx.lifecycle.ViewModel() {
    val skillsDecks = deckRepository.getDecksByCategory("SKILLS_PRACTICE")
}