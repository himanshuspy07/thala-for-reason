package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.engine.ThalaReasonEngine
import com.example.ui.theme.ThalaColors

@Composable
fun MemeGalleryScreen(
    themeColors: ThalaColors,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var activeQuoteIndex by remember { mutableIntStateOf(0) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "THALA MEME DECK",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = themeColors.primary,
                    letterSpacing = 1.5.sp
                )
                Text(
                    text = "Dhoni Legends & Cricket Folklore",
                    fontSize = 12.sp,
                    color = themeColors.textSecondary,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // 1. Captain Cool Quote of the Day Block
        item {
            ElevatedCard(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = themeColors.surface
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FormatQuote,
                        contentDescription = "Quote",
                        tint = themeColors.primary,
                        modifier = Modifier.size(36.dp)
                    )
                    
                    Text(
                        text = ThalaReasonEngine.thalaQuotes[activeQuoteIndex],
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 22.sp
                        ),
                        color = themeColors.textPrimary,
                        textAlign = TextAlign.Center
                    )

                    Button(
                        onClick = {
                            activeQuoteIndex = (activeQuoteIndex + 1) % ThalaReasonEngine.thalaQuotes.size
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = themeColors.surfaceVariant,
                            contentColor = themeColors.primary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Next Dhoni Wisdom \uD83D\uDCD6")
                    }
                }
            }
        }

        // Section header
        item {
            Text(
                text = "Viral Cricket Meme Cards",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = themeColors.textPrimary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // 2. Linear list of funny explainable meme cards
        items(ThalaReasonEngine.thalaMemesList) { item ->
            val title = item.first
            val explanation = item.second

            OutlinedCard(
                border = BorderStroke(1.dp, themeColors.surfaceVariant),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = themeColors.surface
                ),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = themeColors.primary
                            )
                        )
                        
                        // Small Badge
                        Box(
                            modifier = Modifier
                                .background(themeColors.primary.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "JERSEY #7",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = themeColors.primary
                            )
                        }
                    }

                    Text(
                        text = explanation,
                        style = MaterialTheme.typography.bodyMedium,
                        color = themeColors.textPrimary,
                        lineHeight = 18.sp
                    )

                    HorizontalDivider(color = themeColors.surfaceVariant)

                    // Card Action items
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Copy Text
                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Thala Meme", "$title: $explanation")
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "Meme text copied!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.testTag("copy_${title.replace(" ", "_")}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy text",
                                tint = themeColors.textSecondary
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Share Intent
                        IconButton(
                            onClick = {
                                val sendIntent = android.content.Intent().apply {
                                    action = android.content.Intent.ACTION_SEND
                                    putExtra(android.content.Intent.EXTRA_TEXT, "$title: $explanation\n\nUnlocked on 'Thala For A Reason' mobile app! #ThalaForAReason 🦁")
                                    type = "text/plain"
                                }
                                val shareIntent = android.content.Intent.createChooser(sendIntent, "Share Thala Meme via")
                                context.startActivity(shareIntent)
                            },
                            modifier = Modifier.testTag("share_${title.replace(" ", "_")}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share card",
                                tint = themeColors.primary
                            )
                        }
                    }
                }
            }
        }
    }
}
