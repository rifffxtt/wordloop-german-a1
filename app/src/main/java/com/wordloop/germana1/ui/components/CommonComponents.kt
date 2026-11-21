package com.wordloop.germana1.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.VolumeUp

@Composable
fun WordLoopTopAppBar(
    title: String = "WordLoop German A1",
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit,
    showBackButton: Boolean = false,
    onBackClick: (() -> Unit)? = null
) {
    TopAppBar(
        modifier = Modifier.fillMaxWidth(),
        title = { Text(text = title, fontWeight = FontWeight.Bold) },
        navigationIcon = if (showBackButton) {
            {
                IconButton(onClick = onBackClick ?: {}) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
        } else {
            {
                IconButton(onClick = onSearchClick) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            }
        },
        actions = {
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Default.Settings, contentDescription = "Settings")
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    val showClear = remember { mutableStateOf(query.isNotEmpty()) }
    showClear.value = query.isNotEmpty()

    androidx.compose.material3.TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        placeholder = { Text(stringResource(id = com.wordloop.germana1.R.string.search_hint)) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
        trailingIcon = if (showClear.value) {
            {
                IconButton(onClick = onClear) {
                    Icon(Icons.Default.Close, contentDescription = "Clear")
                }
            }
        } else null,
        singleLine = true,
        colors = androidx.compose.material3.TextFieldDefaults.textFieldColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    )
}

@Composable
fun ProgressCard(
    progress: Float,
    current: Int,
    total: Int,
    streak: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Daily Progress", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("$current / $total cards", fontSize = 16.sp)
                Text("🔥 $streak Day Streak", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 12.dp))
            
            androidx.compose.material3.LinearProgressIndicator(
                progress = progress.coerceIn(0f, 1f),
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                trackColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f)
            )
            
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 4.dp))
            Text("${(progress * 100).toInt()}% Complete", fontSize = 12.sp)
        }
    }
}

@Composable
fun QuickPracticeCard(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .height(64.dp),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(start = 16.dp))
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun DeckCard(
    deck: com.wordloop.germana1.data.entity.DeckEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (deck.isLocked) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(deck.title, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Text(deck.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (deck.isLocked) {
                androidx.compose.material3.Icon(
                    androidx.compose.material.icons.filled.Lock,
                    contentDescription = "Locked",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                androidx.compose.material3.Icon(
                    androidx.compose.material.icons.filled.ArrowForwardIos,
                    contentDescription = "Navigate",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun ArticleChip(article: String?, modifier: Modifier = Modifier) {
    if (article == null) return
    
    val (color, textColor) = when (article) {
        "der" -> MaterialTheme.colorScheme.getColor("article_der") to MaterialTheme.colorScheme.onPrimary
        "die" -> MaterialTheme.colorScheme.getColor("article_die") to MaterialTheme.colorScheme.onPrimary
        "das" -> MaterialTheme.colorScheme.getColor("article_das") to MaterialTheme.colorScheme.onPrimary
        else -> MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary
    }

    androidx.compose.material3.Chip(
        modifier = modifier,
        onClick = {},
        colors = androidx.material3.ChipDefaults.chipColors(
            containerColor = androidx.compose.runtime.CompositionLocalProvider(
                androidx.compose.material3.LocalContentColor provides textColor
            ) { color }
        )
    ) {
        Text(
            text = article.uppercase(),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

@Composable
fun MasteryIndicator(level: Int, modifier: Modifier = Modifier) {
    val (color, label) = when (level) {
        0 -> MaterialTheme.colorScheme.getColor("mastery_hard") to "Hard"
        1 -> MaterialTheme.colorScheme.getColor("mastery_good") to "Good"
        2 -> MaterialTheme.colorScheme.getColor("mastery_easy") to "Easy"
        else -> MaterialTheme.colorScheme.getColor("mastery_hard") to "New"
    }

    Box(
        modifier = modifier
            .size(12.dp)
            .background(color, RoundedCornerShape(6.dp))
    )
}

@Composable
fun SpeakerButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    slow: Boolean = false
) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            imageVector = if (slow) Icons.Default.SlowMotionVideo else Icons.Default.VolumeUp,
            contentDescription = if (slow) "Slow Speed" else "Normal Speed",
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun LoadingOverlay(message: String = "Loading...") {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))
            Text(message, fontSize = 16.sp)
        }
    }
}

@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    message: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, size = 64.dp)
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))
        Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
        Text(message, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
    }
}

@Composable
fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 16.dp)
    )
}