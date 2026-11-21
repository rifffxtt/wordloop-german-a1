package com.wordloop.germana1.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHostController
import androidx.navigation.compose.rememberNavController
import com.wordloop.germana1.data.entity.DeckEntity
import com.wordloop.germana1.ui.components.*
import com.wordloop.germana1.ads.AdManager
import com.wordloop.germana1.ads.AdConfig
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import android.util.TypedValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.layout.height
import androidx.compose.ui.unit.px
import com.wordloop.germana1.R

@Composable
fun HomeScreen(
    navController: NavHostController,
    adManager: AdManager
) {
    val viewModel: HomeViewModel = hiltViewModel()
    val context = LocalContext.current
    val showSearch = remember { mutableStateOf(false) }
    val searchQuery = remember { mutableStateOf("") }

    val vocabularyDecks by viewModel.vocabularyDecks.collectAsState(initial = emptyList())
    val grammarDecks by viewModel.grammarDecks.collectAsState(initial = emptyList())
    val skillsDecks by viewModel.skillsDecks.collectAsState(initial = emptyList())
    val dailyProgress by viewModel.dailyProgress.collectAsState(initial = HomeViewModel.DailyProgress(0, 15, false))
    val streak by viewModel.streak.collectAsState(initial = 0)
    val quickPracticeItems by viewModel.quickPracticeDecks.collectAsState(initial = emptyList())

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = androidx.compose.material3.MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            // Top App Bar
            WordLoopTopAppBar(
                onSearchClick = { showSearch.value = true },
                onSettingsClick = { navController.navigate("settings") }
            )

            // Search Bar (when active)
            if (showSearch.value) {
                SearchBar(
                    query = searchQuery.value,
                    onQueryChange = { searchQuery.value = it },
                    onClear = { searchQuery.value = "" },
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // Main Content
            Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                // Daily Progress Card
                ProgressCard(
                    progress = dailyProgress.progress,
                    current = dailyProgress.cardsReviewed,
                    total = dailyProgress.dailyGoal,
                    streak = streak
                )

                // Quick Practice Strip
                QuickPracticeCard(
                    title = "Mistakes / Hard Cards",
                    icon = androidx.compose.material.icons.filled.Refresh,
                    onClick = { navController.navigate("quiz/mistakes") }
                )
                QuickPracticeCard(
                    title = "Article Quiz (Der/Die/Das)",
                    icon = androidx.compose.material.icons.filled.SortByAlpha,
                    onClick = { navController.navigate("quiz/article_picker") }
                )
                QuickPracticeCard(
                    title = "4-Skills Practice",
                    icon = androidx.compose.material.icons.filled.Mic,
                    onClick = { navController.navigate("skills") }
                )

                // Vocabulary Decks
                SectionHeader("Vocabulary (1-12)")
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 100.dp) // Space for banner ad
                ) {
                    items(vocabularyDecks.filter { !it.isLocked }) { deck ->
                        DeckCard(deck = deck, onClick = { navController.navigate("study/${deck.id}") })
                    }
                    items(vocabularyDecks.filter { it.isLocked }) { deck ->
                        LockedDeckCard(deck = deck, adManager = adManager)
                    }
                }
            }
        }
    }
}

@Composable
fun LockedDeckCard(
    deck: DeckEntity,
    adManager: AdManager
) {
    import androidx.compose.material3.Chip
    import androidx.compose.material3.Text
    import androidx.compose.material.icons.filled.Lock
    import androidx.compose.material.icons.filled.PlayCircle
    import androidx.compose.runtime.Composable
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.unit.sp
    import androidx.compose.foundation.layout.Row
    import androidx.compose.foundation.layout.Column
    import androidx.compose.foundation.layout.padding
    import androidx.compose.foundation.layout.fillMaxWidth
    import androidx.compose.foundation.layout.Arrangement
    import androidx.compose.ui.Alignment
    import androidx.compose.material3.Card
    import androidx.compose.material3.CardDefaults

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(deck.title, fontSize = 16.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Medium)
                Text(deck.description, fontSize = 12.sp, color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant)
            }
            androidx.compose.material.icons.filled.Lock
            androidx.compose.material3.Icon(androidx.compose.material.icons.filled.Lock, contentDescription = "Locked")
            
            Chip(
                onClick = { adManager.showRewardedAdSuspend(LocalContext.current as android.app.Activity) },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.material3.Icon(androidx.compose.material.icons.filled.PlayCircle, contentDescription = "Ad")
                    androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(start = 4.dp))
                    Text("Unlock with Ad", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun BannerAdView(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    
    AndroidView(
        factory = { ctx ->
            val adView = AdView(ctx).apply {
                adUnitId = AdConfig.BANNER_AD_UNIT_ID
                adSize = AdSize.BANNER
                loadAd(AdRequest.Builder().build())
            }
            adView
        },
        update = { adView ->
            // AdView handles its own lifecycle
        },
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
    )
}