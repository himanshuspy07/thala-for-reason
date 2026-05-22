package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import kotlin.math.sin
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.engine.ThalaReasonResult
import kotlinx.coroutines.delay
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CinematicThalaOverlay(
    reason: ThalaReasonResult,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var animateStart by remember { mutableStateOf(false) }
    
    // Song lyrics synchronized list for "Bole Jo Koyal Baago Me..." (DHONI ULTIMATE MEME SONG)
    val lyrics = listOf(
        "♪ Bole Jo Koyal Baago Mein... ♪",
        "♪ Yaad Mujhe Bhi Aane Lagi... ♪",
        "♪ Haan Bheegi-Bheegi Raaton Mein... ♪",
        "♪ Thala For A Reason! #7 ♪",
        "♪ MSD is Captain Cool! ♪",
        "♪ 7 Jersey is Not a Number, It is Love! ♪"
    )
    var currentLyricIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        animateStart = true
        while (true) {
            delay(1800)
            currentLyricIndex = (currentLyricIndex + 1) % lyrics.size
        }
    }

    // Infinite transitions for stadium lights and flashy colors
    val infiniteTransition = rememberInfiniteTransition(label = "stadium_lights")
    
    val pulsingGoldScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulsing_gold"
    )

    val flashingColor1 by infiniteTransition.animateColor(
        initialValue = Color(0xFFFFD700),
        targetValue = Color(0xFF0056B3),
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flash_left"
    )

    val flashingColor2 by infiniteTransition.animateColor(
        initialValue = Color(0xFF0056B3),
        targetValue = Color(0xFFFF4500),
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "flash_right"
    )

    val screenShakeOffset by infiniteTransition.animateFloat(
        initialValue = -5f,
        targetValue = 5f,
        animationSpec = infiniteRepeatable(
            animation = tween(80, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "screen_shake"
    )

    val helicopterRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "helicopter"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xE6020512)) // Transparent dark navy stadium back
    ) {
        // 1. Particle Canvas (Simulated Sparkles & Confetti)
        Canvas(modifier = Modifier.fillMaxSize()) {
            val rand = kotlin.random.Random(45)
            // Draw stadium flood lights
            drawCircle(color = flashingColor1.copy(alpha = 0.15f), radius = 350f, center = Offset(0f, 0f))
            drawCircle(color = flashingColor2.copy(alpha = 0.15f), radius = 350f, center = Offset(size.width, 0f))

            // Draw gold sparks
            for (i in 0..45) {
                val cx = rand.nextFloat() * size.width
                val cy = rand.nextFloat() * size.height
                val speedFactor = (sin(helicopterRotation.toDouble() / 50.0 + i) + 1.0) / 2.0
                val radius = (4..12).random(rand).toFloat() * speedFactor.toFloat()
                drawCircle(
                    color = Color(0xFFD4AF37).copy(alpha = (0.3f + 0.7f * speedFactor).toFloat()),
                    radius = radius,
                    center = Offset(cx, cy)
                )
            }
        }

        // 2. Content view (Vibrating to simulate screen shake during helicopter hit!)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(
                    x = if (reason.trackingId == "helicopter_shot") screenShakeOffset.dp else 0.dp,
                    y = if (reason.trackingId == "helicopter_shot") screenShakeOffset.dp else 0.dp
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Top Celebration Header
                ElevatedCard(
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = Color(0x990C101F)
                    ),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.padding(bottom = 24.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = "Unlock",
                            tint = Color(0xFFFFD700)
                        )
                        Text(
                            text = "CINEMATIC CELEBRATION UNLOCKED!",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFD700),
                                letterSpacing = 1.2.sp
                            )
                        )
                    }
                }

                // Spinning Helicopter Shot Visual Badge
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .scale(pulsingGoldScale)
                        .clip(CircleShape)
                        .background(
                            Brush.sweepGradient(
                                colors = listOf(Color(0xFFFFD700), Color(0xFF0056B3), Color(0xFFFFD700))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF080C1E)),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            rotate(helicopterRotation) {
                                // Draw stylized Cricket Bat spinning!
                                drawLine(
                                    color = Color(0xFFFFD700),
                                    start = Offset(size.width / 2, size.height / 2),
                                    end = Offset(size.width / 2, 25f),
                                    strokeWidth = 14f
                                )
                                // Cricket ball flying around
                                drawCircle(
                                    color = Color(0xFFFF3333),
                                    radius = 16f,
                                    center = Offset(size.width / 2, 25f)
                                )
                            }
                        }

                        // Center glowing 7
                        Text(
                            text = "7",
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            fontWeight = FontWeight.Black,
                            fontSize = 62.sp,
                            color = Color(0xFFFFD700)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Cinematic Bold Banner Title
                Text(
                    text = "THALA FOR A REASON",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFFFFD700),
                    textAlign = TextAlign.Center,
                    letterSpacing = 2.sp
                )
                Text(
                    text = "🦁🦁🦁 7 🦁🦁🦁",
                    fontSize = 20.sp,
                    color = Color(0xFFFFD700),
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(18.dp))

                // The Golden Explanation Cards showing math formulas
                OutlinedCard(
                    onClick = {},
                    colors = CardDefaults.outlinedCardColors(
                        containerColor = Color(0xFF0C101F).copy(alpha = 0.85f),
                        contentColor = Color.White
                    ),
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.5.dp, Color(0xFFFFD700))
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Formula: ${reason.expression}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFD700)
                            ),
                            textAlign = TextAlign.Center
                        )
                        HorizontalDivider(color = Color(0x33FFD700))
                        Text(
                            text = reason.explanation,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = Color.White,
                            lineHeight = 22.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Subtitle Lyrics for "Bole Jo Koyal" (extremely interactive and fun!)
                ElevatedCard(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = Color(0xFF10B981).copy(alpha = 0.15f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Audio Icon",
                            tint = Color(0xFF10B981)
                        )
                        Text(
                            text = lyrics[currentLyricIndex],
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = Color(0xFF10B981),
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Button toolbar
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFD700),
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Dismiss, Boss", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
