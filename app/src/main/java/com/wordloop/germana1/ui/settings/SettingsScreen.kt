package com.wordloop.germana1.ui.settings

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
import androidx.compose.material3.Switch
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.platform.LocalContext

@Composable
fun SettingsScreen(
    navController: NavHostController
) {
    val viewModel: SettingsViewModel = hiltViewModel()
    val context = LocalContext.current

    val dailyGoal by viewModel.dailyGoal.collectAsState()
    val ttsRate by viewModel.ttsRate.collectAsState()

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
                title = { Text(stringResource(R.string.settings_title), fontWeight = FontWeight.Bold) },
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

            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Daily Goal Section
                SettingsSection("Daily Goal") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        viewModel.dailyGoals.forEach { goal ->
                            GoalChip(
                                label = "$goal cards",
                                selected = dailyGoal == goal,
                                onClick = { viewModel.setDailyGoal(goal) }
                            )
                        }
                    }
                }

                // TTS Rate Section
                SettingsSection("TTS Speech Rate") {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        viewModel.ttsRates.forEach { rate ->
                            GoalChip(
                                label = when (rate) {
                                    0.75f -> "0.75x"
                                    1.0f -> "1.0x"
                                    1.25f -> "1.25x"
                                    1.5f -> "1.5x"
                                    else -> "${rate}x"
                                },
                                selected = ttsRate == rate,
                                onClick = { viewModel.setTtsRate(rate) }
                            )
                        }
                    }
                }

                // Reset Progress
                SettingsSection("Danger Zone") {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.getColor("mastery_hard").copy(alpha = 0.1f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Warning, contentDescription = "Warning", tint = MaterialTheme.colorScheme.getColor("mastery_hard"))
                                androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(start = 12.dp))
                                Text("Reset All Progress", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.getColor("mastery_hard"))
                            }
                            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 8.dp))
                            Text(stringResource(R.string.confirm_reset), fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(top = 16.dp))
                            Button(
                                onClick = { showResetConfirmation(context, viewModel) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.getColor("mastery_hard"),
                                    contentColor = MaterialTheme.colorScheme.onPrimary
                                )
                            ) {
                                Text("Reset Everything", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Privacy Policy
                SettingsSection("Legal") {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { /* Open privacy policy */ }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(stringResource(R.string.privacy_policy), fontSize = 16.sp)
                            androidx.compose.foundation.layout.Spacer(modifier = Modifier.weight(1f))
                            Icon(androidx.compose.material.icons.filled.ArrowForwardIos, contentDescription = "Open")
                        }
                    }
                }

                // App Version
                Text(
                    "WordLoop German A1 v1.0.0",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth().padding(top = 32.dp)
                )
            }
        }
    }
}

@Composable
fun SettingsSection(title: String, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(title, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        content()
    }
}

@Composable
fun GoalChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    androidx.compose.material3.Chip(
        modifier = modifier.weight(1f),
        onClick = onClick,
        colors = androidx.material3.ChipDefaults.chipColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            selectedContainerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun showResetConfirmation(context: android.content.Context, viewModel: SettingsViewModel) {
    // In a real app, use AlertDialog
    // For simplicity, direct reset with a confirmation state
    val confirmed = remember { mutableStateOf(false) }
    if (!confirmed.value) {
        confirmed.value = true
        // Show confirmation dialog - simplified
        viewModel.resetAllProgress()
    }
}