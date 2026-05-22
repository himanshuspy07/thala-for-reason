package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.HistoryItem
import com.example.ui.theme.ThalaColors
import com.example.ui.viewmodel.ThalaViewModel

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: ThalaViewModel,
    themeColors: ThalaColors,
    modifier: Modifier = Modifier
) {
    val inputState by viewModel.calculatorInput.collectAsState()
    val resultState by viewModel.calculatorResult.collectAsState()
    val historyList by viewModel.history.collectAsState()
    val isAILoading by viewModel.isAILoading.collectAsState()

    val isThalaSeven = resultState.trim() == "7" || resultState.trim() == "7.0" || resultState.trim() == "07"
    
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    var showHistorySection by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Glassmorphic Calculator Display Card
        item {
            ElevatedCard(
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = themeColors.surface
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = if (isThalaSeven) 2.dp else 1.dp,
                        color = if (isThalaSeven) themeColors.primary.copy(alpha = pulseAlpha) else themeColors.surfaceVariant.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(24.dp)
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    // Running formula screen text
                    Text(
                        text = inputState.ifEmpty { "0" },
                        fontSize = 20.sp,
                        fontFamily = FontFamily.Monospace,
                        color = themeColors.textSecondary,
                        textAlign = TextAlign.End,
                        maxLines = 2,
                        modifier = Modifier.fillMaxWidth().testTag("calc_display")
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Resulting display screen
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        // Subtle glow backing effect
                        if (isThalaSeven) {
                            Box(
                                modifier = Modifier
                                    .size(100.dp, 60.dp)
                                    .background(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                themeColors.primary.copy(alpha = 0.25f * pulseAlpha),
                                                Color.Transparent
                                            )
                                        )
                                    )
                            )
                        }
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (isThalaSeven) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(themeColors.primary)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "Thala Mode Active",
                                        color = Color(0xFF040815),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = (-0.2).sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                            }
                            Text(
                                text = resultState,
                                fontSize = if (isThalaSeven) 54.sp else 44.sp,
                                fontWeight = if (isThalaSeven) FontWeight.Black else FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = themeColors.primary,
                                textAlign = TextAlign.End,
                                maxLines = 1,
                                modifier = Modifier.testTag("calc_result")
                            )
                        }
                    }

                    // Meme prompt box directly integrated under the evaluated result
                    if (isThalaSeven) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF0F172A).copy(alpha = 0.5f))
                                .border(1.dp, themeColors.primary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(themeColors.primary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "AI",
                                    color = Color(0xFF040815),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Column(
                                modifier = Modifier.weight(1f),
                                horizontalAlignment = Alignment.Start
                            ) {
                                Text(
                                    text = "\"Because ${inputState.ifEmpty { "7 + 0" }} equals 7.\"",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = themeColors.primary,
                                    style = androidx.compose.ui.text.TextStyle(fontStyle = FontStyle.Italic)
                                )
                                Text(
                                    text = "Message is clear. Thala For A Reason! 🦁",
                                    fontSize = 10.sp,
                                    color = themeColors.textPrimary.copy(alpha = 0.7f),
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Start
                                )
                            }
                        }
                    }
                }
            }
        }

        // Calculator Keys Grid Group (4x5)
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val buttonRows = listOf(
                    listOf("C" to true, "DEL" to true, "%" to true, "÷" to true),
                    listOf("7" to false, "8" to false, "9" to false, "x" to true),
                    listOf("4" to false, "5" to false, "6" to false, "-" to true),
                    listOf("1" to false, "2" to false, "3" to false, "+" to true),
                    listOf("0" to false, "." to false, "=" to true)
                )

                for (row in buttonRows) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        for (btn in row) {
                            val isOp = btn.second
                            val value = btn.first
                            
                            Box(
                                modifier = Modifier
                                    .weight(if (value == "=") 2.0f else 1.0f)
                                    .height(64.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(
                                        brush = if (value == "=") {
                                            Brush.horizontalGradient(
                                                colors = listOf(themeColors.primary, themeColors.primaryVariant)
                                            )
                                        } else if (value == "7") {
                                            SolidColor(themeColors.primary.copy(alpha = 0.1f))
                                        } else if (value == "C") {
                                            SolidColor(Color(0xFF1E293B).copy(alpha = 0.8f))
                                        } else if (value == "DEL" || value == "%") {
                                            SolidColor(Color(0xFF1E293B).copy(alpha = 0.6f))
                                        } else if (value == "÷") {
                                            SolidColor(themeColors.primary)
                                        } else if (isOp) {
                                            SolidColor(Color(0xFF1E293B).copy(alpha = 0.4f))
                                        } else {
                                            SolidColor(Color(0xFF1E293B).copy(alpha = 0.25f))
                                        }
                                    )
                                    .border(
                                        width = if (value == "7") 2.dp else 1.dp,
                                        color = if (value == "7") themeColors.primary else Color(0x33475569),
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                    .clickable {
                                        when (value) {
                                            "C" -> viewModel.onClear()
                                            "DEL" -> viewModel.onDelete()
                                            "=" -> viewModel.onEvaluate()
                                            "x" -> viewModel.onOperatorPressed("*")
                                            "÷" -> viewModel.onOperatorPressed("/")
                                            "+", "-", "%" -> viewModel.onOperatorPressed(value)
                                            else -> viewModel.onDigitPressed(value)
                                        }
                                    }
                                    .testTag("key_$value"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = value,
                                    fontSize = if (isOp) 20.sp else 24.sp,
                                    fontWeight = if (value == "7") FontWeight.Black else FontWeight.Bold,
                                    fontFamily = FontFamily.SansSerif,
                                    color = if (value == "=") {
                                        Color(0xFF040815)
                                    } else if (value == "÷") {
                                        Color(0xFF040815)
                                    } else if (value == "7") {
                                        themeColors.primary
                                    } else if (value == "C") {
                                        Color(0xFFFF4444)
                                    } else if (value == "DEL" || value == "%") {
                                        themeColors.primary
                                    } else if (isOp) {
                                        themeColors.primary
                                    } else {
                                        themeColors.textPrimary
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Beautiful Divider with Jersey Number
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = themeColors.surfaceVariant)
                Text(
                    text = " \uD83C\uDFCF OR DETECT ANY WORD \uD83D\uDC26 ",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = themeColors.primary,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = themeColors.surfaceVariant)
            }
        }

        // Custom AI Word Input Form Drawer
        item {
            ElevatedCard(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = themeColors.surface
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Jersey #7 Text Connector",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = themeColors.textPrimary
                    )
                    
                    OutlinedTextField(
                        value = viewModel.textDetectionInput.value,
                        onValueChange = { viewModel.textDetectionInput.value = it },
                        placeholder = { Text("Type name (e.g., Dhoni, Virat, Coffee)...") },
                        modifier = Modifier.fillMaxWidth().testTag("text_detector_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = themeColors.primary,
                            unfocusedBorderColor = themeColors.surfaceVariant,
                            cursorColor = themeColors.primary,
                            focusedTextColor = themeColors.textPrimary,
                            unfocusedTextColor = themeColors.textPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Standard local formulas analyzer
                        Button(
                            onClick = { viewModel.onAnalyzeTextDetection(useGemini = false) },
                            modifier = Modifier.weight(1f).testTag("local_analysis_btn"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = themeColors.surfaceVariant,
                                contentColor = themeColors.primary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Fast Reveal", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        // Gemini AI Connector
                        Button(
                            onClick = { viewModel.onAnalyzeTextDetection(useGemini = true) },
                            modifier = Modifier.weight(1.2f).testTag("gemini_analysis_btn"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = themeColors.primary,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isAILoading
                        ) {
                            if (isAILoading) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black, strokeWidth = 2.dp)
                            } else {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = "AI", modifier = Modifier.size(16.dp))
                                    Text("AI Deep Link", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Toggle History collapsible header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showHistorySection = !showHistorySection }
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "History",
                        tint = themeColors.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Evaluation History (${historyList.size})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = themeColors.textPrimary
                    )
                }
                Icon(
                    imageVector = if (showHistorySection) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = "Toggle",
                    tint = themeColors.textSecondary
                )
            }
        }

        // List past equations history
        if (showHistorySection) {
            if (historyList.isEmpty()) {
                item {
                    Text(
                        text = "Perform a calculation to build history catalog.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = themeColors.textSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(16.dp)
                    )
                }
            } else {
                items(historyList) { historyItem ->
                    HistoryItemCard(item = historyItem, colors = themeColors)
                }
            }
        }
    }
}

@Composable
fun HistoryItemCard(item: HistoryItem, colors: ThalaColors) {
    ElevatedCard(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = colors.surface
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.expression,
                    fontFamily = FontFamily.Monospace,
                    color = colors.textSecondary,
                    fontSize = 14.sp
                )
                if (item.isThalaTriggered) {
                    SuggestionChip(
                        onClick = {},
                        label = { Text("THALA APPROVED 🦁", fontSize = 10.sp, fontWeight = FontWeight.Black) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = colors.primary.copy(alpha = 0.15f),
                            labelColor = colors.primary
                        ),
                        border = BorderStroke(1.dp, colors.primary.copy(alpha = 0.3f))
                    )
                } else {
                    SuggestionChip(
                        onClick = {},
                        label = { Text("NORMAL", fontSize = 10.sp) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = colors.surfaceVariant,
                            labelColor = colors.textSecondary
                        ),
                        border = BorderStroke(1.dp, colors.surfaceVariant)
                    )
                }
            }
            Text(
                text = "= ${item.result}",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = if (item.isThalaTriggered) colors.primary else colors.textPrimary
            )
            if (item.isThalaTriggered) {
                Text(
                    text = item.explanation,
                    fontSize = 12.sp,
                    color = colors.textPrimary.copy(alpha = 0.9f),
                    lineHeight = 16.sp
                )
            }
        }
    }
}
