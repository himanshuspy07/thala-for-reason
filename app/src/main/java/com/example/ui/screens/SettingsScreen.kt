package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.UserSettings
import com.example.ui.theme.ThalaColors
import com.example.ui.theme.ThalaTheme
import com.example.ui.viewmodel.ThalaViewModel

@Composable
fun SettingsScreen(
    viewModel: ThalaViewModel,
    themeColors: ThalaColors,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val userSettingsState by viewModel.userSettings.collectAsState()
    val settings = userSettingsState ?: UserSettings()

    // Active visual theme helper
    val activeThemeStr = settings.currentThemeStr

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Scroll view Header
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "SETTINGS & BIOGRAPHY",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = themeColors.primary,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "Personalize Thala Aura & Playback",
                    fontSize = 12.sp,
                    color = themeColors.textSecondary,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // Section: Personalization Theme Selection
        item {
            ElevatedCard(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = themeColors.surface
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Aesthetic Theme Packs",
                        fontWeight = FontWeight.Bold,
                        color = themeColors.textPrimary,
                        fontSize = 15.sp
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        ThemeRowItem(
                            title = "Navy & Brushed Gold (Defacto)",
                            description = "The classic premium dark jersey style.",
                            colorSample = Color(0xFFFFD700),
                            isSelected = activeThemeStr == "NAVY_GOLD",
                            onClick = { viewModel.updateTheme(ThalaTheme.NAVY_GOLD) }
                        )

                        ThemeRowItem(
                            title = "Chennai Super Yellow (Captain Cool)",
                            description = "Bright yellow & navy CSK team vibes. (Unlocks at 500 XP)",
                            colorSample = Color(0xFFF9DC02),
                            isEnabled = settings.totalXp >= 500, // Locked until 500 XP!
                            isSelected = activeThemeStr == "CS_YELLOW",
                            onClick = { viewModel.updateTheme(ThalaTheme.CS_YELLOW) }
                        )

                        ThemeRowItem(
                            title = "Bharat Indigo (Team India)",
                            description = "Deep cricket stadium indigo with saffron accents.",
                            colorSample = Color(0xFFFF9933),
                            isSelected = activeThemeStr == "INDIA_BLUE",
                            onClick = { viewModel.updateTheme(ThalaTheme.INDIA_BLUE) }
                        )

                        ThemeRowItem(
                            title = "Neon Turf (Cyber Cricket)",
                            description = "Futuristic neon green pitch style.",
                            colorSample = Color(0xFF22C55E),
                            isSelected = activeThemeStr == "NEON_MINT",
                            onClick = { viewModel.updateTheme(ThalaTheme.NEON_MINT) }
                        )
                    }
                }
            }
        }

        // Section: Hardware feedback control
        item {
            ElevatedCard(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = themeColors.surface
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Vibration & Audio",
                        fontWeight = FontWeight.Bold,
                        color = themeColors.textPrimary,
                        fontSize = 15.sp
                    )

                    // Haptic slider
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Tac-Vibration Strength", fontSize = 13.sp, color = themeColors.textPrimary)
                            Text(text = "${(settings.hapticIntensity * 100).toInt()}%", fontSize = 12.sp, color = themeColors.primary, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = settings.hapticIntensity,
                            onValueChange = { viewModel.setHapticIntensity(it) },
                            valueRange = 0f..1.5f,
                            colors = SliderDefaults.colors(
                                thumbColor = themeColors.primary,
                                activeTrackColor = themeColors.primary,
                                inactiveTrackColor = themeColors.surfaceVariant
                            ),
                            modifier = Modifier.testTag("haptic_slider")
                        )
                    }

                    // Sound switch
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Stadium SFX audio indicators", fontSize = 13.sp, color = themeColors.textPrimary)
                        Switch(
                            checked = settings.soundEnabled,
                            onCheckedChange = { viewModel.toggleSound(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = themeColors.primary,
                                checkedTrackColor = themeColors.primary.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier.testTag("sound_switch")
                        )
                    }
                }
            }
        }

        // Section: Dhoni Legend Bio Brief Card
        item {
            ElevatedCard(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = themeColors.surface
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.SportsCricket, contentDescription = "Cricket", tint = themeColors.primary)
                        Text(
                            text = "Who is Thala? (MS Dhoni)",
                            fontWeight = FontWeight.Bold,
                            color = themeColors.textPrimary,
                            fontSize = 15.sp
                        )
                    }
                    Text(
                        text = "Mahendra Singh Dhoni, affectionately known as Thala (meaning elder brother/leader), is India's most decorated cricket captain. Wearing the holy jersey #7, he led India to victory in the 2007 ICC World T20, the 2011 ICC World Cup, and the 2013 ICC Champions Trophy. His sheer calm temperament under extreme pressure, iconic helicopter hit, and lightning-fast stumpings transformed jersey '7' into the biggest viral meme in sporting history.",
                        fontSize = 12.sp,
                        color = themeColors.textSecondary,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        // Section: Factory clear data
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TextButton(
                    onClick = {
                        viewModel.clearAllHistory()
                        Toast.makeText(context, "Calculations history wiped!", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    ),
                    modifier = Modifier.testTag("wipe_db_btn")
                ) {
                    Icon(imageVector = Icons.Default.DeleteForever, contentDescription = "Wipe")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Clear Evaluation Catalog History")
                }
            }
        }
    }
}

@Composable
fun ThemeRowItem(
    title: String,
    description: String,
    colorSample: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
    isEnabled: Boolean = true
) {
    val alpha = if (isEnabled) 1.0f else 0.45f
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) colorSample.copy(alpha = 0.08f) else Color.Transparent)
            .clickable(enabled = isEnabled, onClick = onClick)
            .padding(12.dp)
            .testTag(title.replace(" ", "_")),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Bullet circle of selected theme color
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(colorSample)
                .border(
                    BorderStroke(2.dp, if (isSelected) Color.White else Color.Transparent),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = Color.Black,
                    modifier = Modifier.size(14.dp)
                )
            } else if (!isEnabled) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = Color.Black,
                    modifier = Modifier.size(12.dp)
                )
            }
        }

        Column(modifier = Modifier.weight(1f).background(Color.Transparent)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color.White.copy(alpha = alpha)
            )
            Text(
                text = description,
                fontSize = 11.sp,
                color = Color.White.copy(alpha = alpha * 0.65f)
            )
        }
    }
}
