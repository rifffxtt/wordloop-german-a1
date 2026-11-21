package com.wordloop.germana1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.hilt.navigation.compose.hiltNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.getString
import androidx.navigation.compose.navArgument
import com.wordloop.germana1.ads.AdManager
import com.wordloop.germana1.tts.TtsManager
import com.wordloop.germana1.ui.customcard.CustomCardScreen
import com.wordloop.germana1.ui.home.HomeScreen
import com.wordloop.germana1.ui.quiz.QuizScreen
import com.wordloop.germana1.ui.quiz.QuizViewModel
import com.wordloop.germana1.ui.quiz.SkillsPracticeScreen
import com.wordloop.germana1.ui.settings.SettingsScreen
import com.wordloop.germana1.ui.study.StudyScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val adManager: AdManager by viewModels()
    private val ttsManager: TtsManager by viewModels()

    private val navController = hiltNavController()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Preload ads
        adManager.preloadInterstitialAd()
        adManager.preloadRewardedAd()
        setContent {
            MaterialTheme {
                Surface {
                    NavHost(navController, startDestination = "home") {
                        composable("home") {
                            HomeScreen(navController, adManager)
                        }
                        composable(
                            route = "study/{deckId}",
                            arguments = listOf(navArgument("deckId") { type = androidx.navigation.NavType.IntType })
                        ) { backStackEntry ->
                            val deckId = backStackEntry.getInt("deckId") ?: 1
                            StudyScreen(navController, deckId, ttsManager)
                        }
                        composable(
                            route = "quiz/{mode}/{deckId}",
                            arguments = listOf(
                                navArgument("mode") { type = androidx.navigation.NavType.StringType },
                                navArgument("deckId") { type = androidx.navigation.NavType.IntType }
                            )
                        ) { backStackEntry ->
                            val modeStr = backStackEntry.getString("mode") ?: "ARTICLE_PICKER"
                            val deckId = backStackEntry.getInt("deckId") ?: 1
                            val mode = QuizViewModel.QuizMode.valueOf(modeStr)
                            QuizScreen(navController, deckId, mode, ttsManager)
                        }
                        composable("quiz/mistakes") {
                            QuizScreen(navController, 0, QuizViewModel.QuizMode.ARTICLE_PICKER, ttsManager)
                        }
                        composable("quiz/article_picker") {
                            QuizScreen(navController, 0, QuizViewModel.QuizMode.ARTICLE_PICKER, ttsManager)
                        }
                        composable("skills") {
                            SkillsPracticeScreen(navController, ttsManager)
                        }
                        composable(
                            route = "quiz/mixed/{deckId}",
                            arguments = listOf(navArgument("deckId") { type = androidx.navigation.NavType.IntType })
                        ) { backStackEntry ->
                            val deckId = backStackEntry.getInt("deckId") ?: 1
                            QuizScreen(navController, deckId, QuizViewModel.QuizMode.MIXED, ttsManager)
                        }
                        composable("settings") {
                            SettingsScreen(navController)
                        }
                        composable("custom_card") {
                            CustomCardScreen(navController)
                        }
                        composable(
                            route = "custom_card/{cardId}",
                            arguments = listOf(navArgument("cardId") { type = androidx.navigation.NavType.IntType })
                        ) { backStackEntry ->
                            val cardId = backStackEntry.getInt("cardId") ?: 0
                            // For editing, we'd need to fetch the card
                            CustomCardScreen(navController)
                        }
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ttsManager.shutdown()
    }
}