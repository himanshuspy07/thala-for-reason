package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.database.UserSettings
import com.example.ui.components.CinematicThalaOverlay
import com.example.ui.theme.ThalaTheme
import com.example.ui.theme.ThalaThemeConfig
import com.example.ui.viewmodel.ThalaViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainScaffold(
    viewModel: ThalaViewModel = viewModel()
) {
    // Current route in standard local navigation state
    var currentRoute by remember { mutableStateOf("calculator") }

    val userSettingsState by viewModel.userSettings.collectAsState()
    val isCinematicActive by viewModel.isCinematicActive.collectAsState()
    val activeReason by viewModel.activeReason.collectAsState()

    // Resolve colors based on theme setup
    val currentTheme = when (userSettingsState?.currentThemeStr) {
        "CS_YELLOW" -> ThalaTheme.CS_YELLOW
        "INDIA_BLUE" -> ThalaTheme.INDIA_BLUE
        "NEON_MINT" -> ThalaTheme.NEON_MINT
        else -> ThalaTheme.NAVY_GOLD
    }

    val thalaColors = ThalaThemeConfig.getColors(currentTheme)

    MaterialTheme(
        colorScheme = darkColorScheme(
            background = thalaColors.background,
            surface = thalaColors.surface,
            primary = thalaColors.primary,
            secondary = thalaColors.secondary,
            onBackground = thalaColors.textPrimary,
            onSurface = thalaColors.textPrimary
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(thalaColors.background)
        ) {
            Scaffold(
                topBar = {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Grade-A Golden Box with custom shadow/glow matching Sophisticated Dark
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(Color(0xFFFFD700), Color(0xFFB8860B))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "7",
                                    color = Color(0xFF040815),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 22.sp,
                                    style = LocalTextStyle.current.copy(
                                        fontStyle = FontStyle.Italic
                                    )
                                )
                            }
                            Column {
                                Text(
                                    text = "Thala For A Reason",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFD700),
                                    letterSpacing = (-0.2).sp
                                )
                                Text(
                                    text = "Captain Cool Edition v7.0",
                                    fontSize = 10.sp,
                                    color = thalaColors.textSecondary.copy(alpha = 0.6f),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                        
                        // Settings top shortcut button
                        IconButton(
                            onClick = { currentRoute = "settings" },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(thalaColors.surfaceVariant.copy(alpha = 0.5f))
                                .border(1.dp, thalaColors.surfaceVariant, CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = thalaColors.textPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                bottomBar = {
                    Column {
                        HorizontalDivider(
                            color = thalaColors.surfaceVariant.copy(alpha = 0.4f),
                            thickness = 1.dp
                        )
                        NavigationBar(
                            containerColor = Color(0xFF02040A).copy(alpha = 0.9f),
                            contentColor = thalaColors.textSecondary,
                            modifier = Modifier.navigationBarsPadding() // Protects bottom gesture pills
                        ) {
                        NavigationBarItem(
                            selected = currentRoute == "calculator",
                            onClick = { currentRoute = "calculator" },
                            label = { Text("Calculator", color = if (currentRoute == "calculator") thalaColors.primary else thalaColors.textSecondary) },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Calculate,
                                    contentDescription = "Calculator",
                                    tint = if (currentRoute == "calculator") thalaColors.primary else thalaColors.textSecondary
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = thalaColors.surfaceVariant
                            )
                        )
                        NavigationBarItem(
                            selected = currentRoute == "memes",
                            onClick = { currentRoute = "memes" },
                            label = { Text("Memes", color = if (currentRoute == "memes") thalaColors.primary else thalaColors.textSecondary) },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.PhotoLibrary,
                                    contentDescription = "Meme Pack Gallery",
                                    tint = if (currentRoute == "memes") thalaColors.primary else thalaColors.textSecondary
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = thalaColors.surfaceVariant
                            )
                        )
                        NavigationBarItem(
                            selected = currentRoute == "achievements",
                            onClick = { currentRoute = "achievements" },
                            label = { Text("Unlocks", color = if (currentRoute == "achievements") thalaColors.primary else thalaColors.textSecondary) },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.EmojiEvents,
                                    contentDescription = "Achievements",
                                    tint = if (currentRoute == "achievements") thalaColors.primary else thalaColors.textSecondary
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = thalaColors.surfaceVariant
                            )
                        )
                        NavigationBarItem(
                            selected = currentRoute == "settings",
                            onClick = { currentRoute = "settings" },
                            label = { Text("Settings", color = if (currentRoute == "settings") thalaColors.primary else thalaColors.textSecondary) },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Settings",
                                    tint = if (currentRoute == "settings") thalaColors.primary else thalaColors.textSecondary
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = thalaColors.surfaceVariant
                            )
                        )
                    }
                }
            },
            containerColor = Color.Transparent
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    // Page Content Switch with beautiful fade transitions
                    AnimatedContent(
                        targetState = currentRoute,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(220)) with fadeOut(animationSpec = tween(220))
                        },
                        modifier = Modifier.fillMaxSize(),
                        label = "screen_navigation"
                    ) { targetScreen ->
                        when (targetScreen) {
                            "calculator" -> CalculatorScreen(viewModel = viewModel, themeColors = thalaColors)
                            "memes" -> MemeGalleryScreen(themeColors = thalaColors)
                            "achievements" -> AchievementsScreen(viewModel = viewModel, themeColors = thalaColors)
                            "settings" -> SettingsScreen(viewModel = viewModel, themeColors = thalaColors)
                        }
                    }
                }
            }

            // High-Performance Full-Screen Animated Cinematic Celebration Overlay
            AnimatedVisibility(
                visible = isCinematicActive && activeReason != null,
                enter = fadeIn(animationSpec = tween(500)),
                exit = fadeOut(animationSpec = tween(500))
            ) {
                activeReason?.let { reason ->
                    CinematicThalaOverlay(
                        reason = reason,
                        onDismiss = { viewModel.dismissCinematic() }
                    )
                }
            }
        }
    }
}
