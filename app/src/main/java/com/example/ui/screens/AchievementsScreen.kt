package com.example.ui.screens

import android.text.format.DateFormat
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.Achievement
import com.example.data.database.UserSettings
import com.example.ui.theme.ThalaColors
import com.example.ui.viewmodel.ThalaViewModel
import java.util.Date

@Composable
fun AchievementsScreen(
    viewModel: ThalaViewModel,
    themeColors: ThalaColors,
    modifier: Modifier = Modifier
) {
    val achievements by viewModel.achievements.collectAsState()
    val userSettingsState by viewModel.userSettings.collectAsState()
    
    val settings = userSettingsState ?: UserSettings()
    val playerXp = settings.totalXp
    val playerLevel = (playerXp / 100) + 1
    val currentXpProgress = (playerXp % 100) / 100f

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // App Header Title
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "THALA TROPHY ROOM",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = themeColors.primary,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "Player Levelling & Trophy Progress",
                    fontSize = 12.sp,
                    color = themeColors.textSecondary,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // 1. Level & XP Progress Card
        item {
            ElevatedCard(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = themeColors.surface
                ),
                modifier = Modifier.fillMaxWidth().testTag("player_stats")
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Level badge
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .background(themeColors.primary.copy(alpha = 0.12f), CircleShape)
                            .border(BorderStroke(2.dp, themeColors.primary), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "LVL",
                                fontSize = 10.sp,
                                color = themeColors.primary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "$playerLevel",
                                fontSize = 24.sp,
                                color = themeColors.textPrimary,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    // XP and Streak progress text
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                text = "Gamer XP: $playerXp pts",
                                fontWeight = FontWeight.Bold,
                                color = themeColors.textPrimary,
                                fontSize = 16.sp
                            )
                            
                            // Streak tracker Fire badge
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = "Streak Fire",
                                    tint = Color(0xFFFF5722),
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "${settings.currentStreak} Day Streak",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFF5722)
                                )
                            }
                        }

                        // Progress rating bar
                        LinearProgressIndicator(
                            progress = { currentXpProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = themeColors.primary,
                            trackColor = themeColors.surfaceVariant
                        )
                        Text(
                            text = "${(currentXpProgress * 100).toInt()}% towards next Level",
                            fontSize = 11.sp,
                            color = themeColors.textSecondary
                        )
                    }
                }
            }
        }

        // Achievements Section Headers
        item {
            Text(
                text = "My Dhoni Achievements",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = themeColors.textPrimary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Achievements List representation
        items(achievements) { achievement ->
            AchievementItemCard(achievement = achievement, colors = themeColors)
        }
    }
}

@Composable
fun AchievementItemCard(achievement: Achievement, colors: ThalaColors) {
    val isUnlocked = achievement.isUnlocked
    val tintColor = if (isUnlocked) colors.primary else colors.textSecondary.copy(alpha = 0.5f)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colors.surface
        ),
        border = BorderStroke(
            width = if (isUnlocked) 1.5.dp else 1.dp,
            color = if (isUnlocked) colors.primary else colors.surfaceVariant
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Icon Badge
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(
                        if (isUnlocked) colors.primary.copy(alpha = 0.12f) else colors.surfaceVariant,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = getBadgeIcon(achievement.badgeIconName),
                    contentDescription = achievement.title,
                    tint = tintColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            // Description Info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = achievement.title,
                        fontWeight = FontWeight.Bold,
                        color = if (isUnlocked) colors.textPrimary else colors.textSecondary,
                        fontSize = 15.sp
                    )
                    
                    Text(
                        text = "+${achievement.xpReward} XP",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = colors.secondary
                    )
                }

                Text(
                    text = achievement.description,
                    fontSize = 12.sp,
                    color = if (isUnlocked) colors.textSecondary else colors.textSecondary.copy(alpha = 0.7f),
                    lineHeight = 16.sp
                )

                if (isUnlocked && achievement.unlockedAt > 0) {
                    val dateFormatted = DateFormat.format("MMM dd yyyy, hh:mm a", Date(achievement.unlockedAt)).toString()
                    Text(
                        text = "Unlocked: $dateFormatted",
                        fontSize = 10.sp,
                        color = colors.primary,
                        fontWeight = FontWeight.Medium
                    )
                } else if (!isUnlocked) {
                    Text(
                        text = "LOCKED",
                        fontSize = 10.sp,
                        color = colors.textSecondary.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// Map key badges to standard material icons safely
private fun getBadgeIcon(badge: String): ImageVector {
    return when (badge) {
        "seven" -> Icons.Default.WorkspacePremium
        "bat" -> Icons.Default.SportsCricket
        "bird" -> Icons.Default.MusicNote
        "lion" -> Icons.Default.Pets
        "crown" -> Icons.Default.EmojiEvents
        else -> Icons.Default.Lock
    }
}
