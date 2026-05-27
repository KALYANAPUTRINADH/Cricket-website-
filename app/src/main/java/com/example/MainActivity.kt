package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.database.JournalDatabase
import com.example.data.model.JournalEntry
import com.example.data.repository.JournalRepository
import com.example.ui.theme.AstralGold
import com.example.ui.theme.CosmicBackground
import com.example.ui.theme.CosmicSurface
import com.example.ui.theme.CosmicSurfaceVariant
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NebulaViolet
import com.example.ui.theme.StardustTeal
import com.example.ui.viewmodel.CosmicViewModel
import com.example.ui.viewmodel.CosmicViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val context = LocalContext.current
                val db: JournalDatabase = remember { JournalDatabase.getDatabase(context) }
                val repository: JournalRepository = remember { JournalRepository(db.journalDao()) }
                val factory: CosmicViewModelFactory = remember { CosmicViewModelFactory(repository) }
                val viewModel: CosmicViewModel = viewModel(factory = factory)

                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("main_scaffold"),
                    containerColor = CosmicBackground
                ) { innerPadding ->
                    // Center all content and restrict width to 600dp on wider tablet screens (Adaptive design)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        contentAlignment = Alignment.TopCenter
                    ) {
                        CosmicAppContent(
                            viewModel = viewModel,
                            modifier = Modifier
                                .fillMaxHeight()
                                .widthIn(max = 600.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CosmicAppContent(
    viewModel: CosmicViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    val apiError by viewModel.apiError.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Identity Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "C O S M I C   M I N D",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = StardustTeal,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 4.sp
                    ),
                    modifier = Modifier.testTag("app_identity_title")
                )
                Text(
                    text = "AI Mind Companion & Cosmic Sanctuary",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.White.copy(alpha = 0.6f),
                        letterSpacing = 1.sp
                    ),
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }

        // Error notification bar (Fades in if API triggers empty or placeholder state)
        AnimatedVisibility(
            visible = apiError != null,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2C0C14)),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFE53935)),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .testTag("api_error_card")
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Error notification",
                        tint = Color(0xFFFF5252),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = apiError ?: "",
                        color = Color(0xFFFFCDD2),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Active operation loader indicator
        AnimatedVisibility(visible = isLoading) {
            LinearProgressIndicator(
                color = StardustTeal,
                trackColor = CosmicSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(CircleShape)
                    .padding(vertical = 4.dp)
            )
        }

        // Navigation Tabs Bar (Within single structured layout as required by constraint)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .background(CosmicSurface, RoundedCornerShape(14.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val tabButtons = listOf(
                Pair("Sanctuary", Icons.Filled.SelfImprovement),
                Pair("Journals", Icons.Filled.MenuBook),
                Pair("Oracle", Icons.Filled.ChatBubble)
            )

            // Let's create beautiful tab pills
            tabButtons.forEachIndexed { index, (label, icon) ->
                val isSelected = selectedTab == index
                val tabBgColor by animateColorAsState(
                    targetValue = if (isSelected) NebulaViolet else Color.Transparent,
                    animationSpec = tween(durationMillis = 200)
                )
                val tabContentColor by animateColorAsState(
                    targetValue = if (isSelected) Color.White else Color.White.copy(alpha = 0.5f),
                    animationSpec = tween(durationMillis = 200)
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(tabBgColor)
                        .clickable { selectedTab = index }
                        .padding(vertical = 8.dp, horizontal = 4.dp)
                        .testTag("tab_button_$index"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            tint = tabContentColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = tabContentColor
                            )
                        )
                    }
                }
            }
        }

        // Primary Dynamic View Container
        AnimatedContent(
            targetState = selectedTab,
            transitionSpec = {
                fadeIn(animationSpec = tween(220, delayMillis = 90)) togetherWith
                fadeOut(animationSpec = tween(90))
            },
            label = "tab_transition",
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { targetIndex ->
            when (targetIndex) {
                0 -> SanctuaryTabScreen(viewModel = viewModel)
                1 -> JournalTabScreen(viewModel = viewModel)
                2 -> OracleTabScreen(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun SanctuaryTabScreen(
    viewModel: CosmicViewModel
) {
    val phase by viewModel.breathingPhase.collectAsStateWithLifecycle()
    val countdown by viewModel.breathingCountdown.collectAsStateWithLifecycle()
    val entries by viewModel.journalEntries.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Core Card 1: Sacred Breathing Pulse
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CosmicSurface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("breathing_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Diaphragmatic Box Breathing",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = StardustTeal,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Slowing your breath resets your central nervous system.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.5f),
                            textAlign = TextAlign.Center
                        ),
                        modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
                    )

                    // Gorgeous Breathing Visual Animation Circle
                    // Map breathing phase to continuous size scales
                    val scaleTarget = when (phase) {
                        "Inhale slowly" -> 1.0f
                        "Hold your breath" -> 1.0f
                        "Exhale peacefully" -> 0.4f
                        "Hold with empty lungs" -> 0.4f
                        else -> 0.7f
                    }

                    val scaleAnimated by animateFloatAsState(
                        targetValue = scaleTarget,
                        animationSpec = tween(durationMillis = 4000, easing = LinearEasing),
                        label = "breathing_circle_scale"
                    )

                    Box(
                        modifier = Modifier
                            .size(160.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Ambient Pulsating Background Circle Shadow
                        Box(
                            modifier = Modifier
                                .fillMaxSize(scaleAnimated)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            NebulaViolet.copy(alpha = 0.4f),
                                            StardustTeal.copy(alpha = 0.1f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )

                        // Core Solid Interactive Floating Circle
                        Box(
                            modifier = Modifier
                                .fillMaxSize(scaleAnimated * 0.75f)
                                .clip(CircleShape)
                                .border(
                                    width = 2.dp,
                                    brush = Brush.horizontalGradient(
                                        listOf(NebulaViolet, StardustTeal)
                                    ),
                                    shape = CircleShape
                                )
                                .background(CosmicSurfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = countdown.toString(),
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    text = "SEC",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = StardustTeal,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text(
                        text = phase,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 0.5.sp
                        ),
                        modifier = Modifier.testTag("breathing_phase_text")
                    )
                }
            }
        }

        // Core Card 2: Interactive Mood Logger Companion
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CosmicSurface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                MoodQuickLoggerSection(viewModel = viewModel)
            }
        }

        // Core Card 3: Bezier Wave Mood Trend Canvas Graph
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = CosmicSurface),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("mood_graph_card")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Your Cosmic Rhythm Chart",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = StardustTeal,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Mood indexes mapped across historic database sequences.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.White.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    val moodHistory = entries.take(7).reversed()
                    if (moodHistory.size < 2) {
                        // Elegant Empty State placeholder pattern
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .background(CosmicSurfaceVariant, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "No data yet",
                                    tint = AstralGold.copy(alpha = 0.5f),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Log at least 2 entries to ignite custom geometric waves.",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color.White.copy(alpha = 0.4f),
                                        textAlign = TextAlign.Center
                                    )
                                )
                            }
                        }
                    } else {
                        // Custom Canvas drawing smooth bezier mindwaves
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .background(CosmicSurfaceVariant, RoundedCornerShape(12.dp))
                                .padding(horizontal = 16.dp, vertical = 20.dp)
                        ) {
                            val width = size.width
                            val height = size.height
                            val spacingX = width / (moodHistory.size - 1)

                            val points = moodHistory.mapIndexed { idx, entry ->
                                // y scale: mood score from 1-5 maps from height to 0
                                val relativeY = height - ((entry.moodScore - 1) / 4f) * height
                                Offset(idx * spacingX, relativeY)
                            }

                            val path = Path().apply {
                                if (points.isNotEmpty()) {
                                    moveTo(points[0].x, points[0].y)
                                    for (i in 0 until points.size - 1) {
                                        val p1 = points[i]
                                        val p2 = points[i + 1]
                                        // Draw smooth quadratic bezier curves between sequences
                                        val controlX = (p1.x + p2.x) / 2
                                        quadraticTo(controlX, p1.y, controlX, p2.y)
                                        lineTo(p2.x, p2.y)
                                    }
                                }
                            }

                            // Draw continuous glowing line path
                            drawPath(
                                path = path,
                                brush = Brush.horizontalGradient(
                                    listOf(NebulaViolet, StardustTeal)
                                ),
                                style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                            )

                            // Highlight coordinates with active custom circles
                            points.forEachIndexed { index, point ->
                                drawCircle(
                                    color = StardustTeal,
                                    radius = 6.dp.toPx(),
                                    center = point
                                )
                                drawCircle(
                                    color = NebulaViolet,
                                    radius = 3.dp.toPx(),
                                    center = point
                                )
                            }
                        }

                        // Bottom descriptors
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Older logs",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.4f)
                            )
                            Text(
                                text = "Most recent log",
                                style = MaterialTheme.typography.labelSmall,
                                color = StardustTeal
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MoodQuickLoggerSection(
    viewModel: CosmicViewModel
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var activeMood by remember { mutableStateOf(3) }

    val moodMetrics = listOf(
        Pair("Void", Color(0xFF9E9E9E)),
        Pair("Eclipse", Color(0xFF8E24AA)),
        Pair("Orbit", Color(0xFF03A9F4)),
        Pair("Aurora", Color(0xFF26A69A)),
        Pair("Supernova", Color(0xFFFFB300))
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Text(
            text = "Track your Active Aura",
            style = MaterialTheme.typography.titleMedium.copy(
                color = StardustTeal,
                fontWeight = FontWeight.Bold
            )
        )
        Text(
            text = "Describe your state to build a persistent mind scape.",
            style = MaterialTheme.typography.bodySmall.copy(
                color = Color.White.copy(alpha = 0.5f)
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Mood Score indicators row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            moodMetrics.forEachIndexed { index, (label, color) ->
                val score = index + 1
                val isSelected = activeMood == score

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { activeMood = score }
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) color else CosmicSurfaceVariant)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.1f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = score.toString(),
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = label,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (isSelected) color else Color.White.copy(alpha = 0.4f),
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 9.sp
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // TextInput Fields
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Log Title (e.g., Midnight Reflections)") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("mood_log_title_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NebulaViolet,
                unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                focusedLabelColor = NebulaViolet,
                unfocusedLabelColor = Color.White.copy(alpha = 0.4f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("What elements fill your mind state?") },
            maxLines = 3,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("mood_log_content_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NebulaViolet,
                unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                focusedLabelColor = NebulaViolet,
                unfocusedLabelColor = Color.White.copy(alpha = 0.4f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (title.isNotBlank() && content.isNotBlank()) {
                    viewModel.addJournalEntry(title, content, activeMood)
                    // Reset fields elegantly
                    title = ""
                    content = ""
                }
            },
            enabled = title.isNotBlank() && content.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("save_mood_log_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = NebulaViolet,
                contentColor = Color.White,
                disabledContainerColor = CosmicSurfaceVariant
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add"
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Save State to Room",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}

@Composable
fun JournalTabScreen(
    viewModel: CosmicViewModel
) {
    val entries by viewModel.journalEntries.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val activeAnalysis by viewModel.analysisResult.collectAsStateWithLifecycle()

    var selectedEntryAnalysis by remember { mutableStateOf<JournalEntry?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        if (entries.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = "Empty sanctuary books",
                        tint = StardustTeal.copy(alpha = 0.4f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Your Mirror Journal is clean.",
                        color = Color.White.copy(alpha = 0.6f),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Save entries in the Sanctuary tab to decode dream schemas and patterns with Gemini AI.",
                        color = Color.White.copy(alpha = 0.4f),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp, start = 24.dp, end = 24.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items = entries) { entry ->
                    JournalEntryItemCard(
                        entry = entry,
                        onDecodeClick = {
                            viewModel.decodeEntryWithAI(entry)
                        },
                        onDeleteClick = {
                            viewModel.deleteEntry(entry)
                        },
                        onViewAnalysisClick = {
                            selectedEntryAnalysis = entry
                        }
                    )
                }
            }
        }

        // Expanded Analysis Floating Dialogue Overlay
        AnimatedVisibility(
            visible = selectedEntryAnalysis != null,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            selectedEntryAnalysis?.let { entry ->
                // Look up any reactive updates to this entry's analysis
                val currentEntry = entries.find { it.id == entry.id } ?: entry

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .shadow(16.dp, RoundedCornerShape(16.dp))
                        .testTag("analysis_overlay_card"),
                    colors = CardDefaults.cardColors(containerColor = CosmicSurfaceVariant),
                    border = BorderStroke(1.dp, StardustTeal.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "A L C H E M I S T   D E C O D E",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = StardustTeal,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 2.sp
                                )
                            )
                            IconButton(onClick = { selectedEntryAnalysis = null }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close",
                                    tint = Color.White.copy(alpha = 0.6f)
                                )
                            }
                        }

                        Text(
                            text = currentEntry.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        val analysisText = currentEntry.analysis
                        if (analysisText != null) {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(260.dp)
                                    .background(CosmicSurface, RoundedCornerShape(8.dp))
                                    .padding(12.dp)
                            ) {
                                item {
                                    Text(
                                        text = analysisText,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            lineHeight = 18.sp
                                        ),
                                        color = Color.White.copy(alpha = 0.9f)
                                    )
                                }
                            }
                        } else {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "This entry has not been analyzed with AI mirror yet.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.5f),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 24.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = { viewModel.decodeEntryWithAI(currentEntry) },
                                    colors = ButtonDefaults.buttonColors(containerColor = NebulaViolet)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = "Mirror analysis"
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Decode Symbols Now")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun JournalEntryItemCard(
    entry: JournalEntry,
    onDecodeClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onViewAnalysisClick: () -> Unit
) {
    val dateString = remember(entry.timestamp) {
        val formatter = SimpleDateFormat("MMM d, yyyy • hh:mm a", Locale.getDefault())
        formatter.format(Date(entry.timestamp))
    }

    val moodColor = when (entry.moodScore) {
        1 -> Color(0xFF9E9E9E)
        2 -> Color(0xFF8E24AA)
        3 -> Color(0xFF03A9F4)
        4 -> Color(0xFF26A69A)
        else -> Color(0xFFFFB300)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("journal_entry_card_${entry.id}"),
        colors = CardDefaults.cardColors(containerColor = CosmicSurface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mood Badge Index
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(moodColor.copy(alpha = 0.15f))
                        .border(1.dp, moodColor.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Mood: ${entry.moodScore}/5",
                        color = moodColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = onDeleteClick,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("delete_journal_button_${entry.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete entry",
                        tint = Color.White.copy(alpha = 0.3f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Text Metadata
            Text(
                text = entry.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = dateString,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.35f),
                modifier = Modifier.padding(vertical = 2.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = entry.content,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // AI analysis checker
                val alreadyAnalyzed = entry.analysis != null
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (alreadyAnalyzed) Icons.Default.CheckCircle else Icons.Default.Info,
                        contentDescription = "Analysis Check",
                        tint = if (alreadyAnalyzed) StardustTeal else Color.White.copy(alpha = 0.2f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (alreadyAnalyzed) "AI Decoded" else "Not Decoded",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (alreadyAnalyzed) StardustTeal else Color.White.copy(alpha = 0.35f),
                        fontWeight = FontWeight.Medium
                    )
                }

                // Call Action Buttons
                if (alreadyAnalyzed) {
                    Button(
                        onClick = onViewAnalysisClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CosmicSurfaceVariant,
                            contentColor = StardustTeal
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                        modifier = Modifier
                            .height(32.dp)
                            .testTag("view_analysis_button_${entry.id}")
                    ) {
                        Text("View Mirror", style = MaterialTheme.typography.labelSmall)
                    }
                } else {
                    Button(
                        onClick = onDecodeClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NebulaViolet,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                        modifier = Modifier
                            .height(32.dp)
                            .testTag("decode_symbols_button_${entry.id}")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("AI Decode", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OracleTabScreen(
    viewModel: CosmicViewModel
) {
    val response by viewModel.geminiOracleResponse.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    var prompt by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 12.dp)
    ) {
        // High quality static header dialogue description
        Card(
            colors = CardDefaults.cardColors(containerColor = CosmicSurface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(NebulaViolet.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Oracle AI Spark",
                        tint = NebulaViolet,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Cosmic Oracle Mirror",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Ask your deepest queries. The Zen AI elder answers with galactic analogies.",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.45f)
                    )
                }
            }
        }

        // Active conversation screen output logs
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(CosmicSurface, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            if (response == null) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Help,
                        contentDescription = "Cosmic Mind helper icon",
                        tint = StardustTeal.copy(alpha = 0.25f),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "The Silent Sanctuary awaits.",
                        color = Color.White.copy(alpha = 0.5f),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "\"Why am I repeating old cycles?\" or \"Guide me through anxiety.\"",
                        color = Color.White.copy(alpha = 0.35f),
                        style = MaterialTheme.typography.labelSmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    item {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(StardustTeal.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = StardustTeal,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Cosmic Master",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = StardustTeal
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = response ?: "",
                                style = MaterialTheme.typography.bodySmall.copy(lineHeight = 20.sp),
                                color = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier
                                    .testTag("oracle_response_text")
                                    .padding(start = 4.dp)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { viewModel.clearOracleResponse() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CosmicSurfaceVariant,
                                    contentColor = Color.White.copy(alpha = 0.6f)
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text("Clear Response", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Input row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = prompt,
                onValueChange = { prompt = it },
                placeholder = { Text("Consult the oracle of stars...") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("oracle_input_field"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NebulaViolet,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                    focusedContainerColor = CosmicSurface,
                    unfocusedContainerColor = CosmicSurface,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                maxLines = 2
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(
                onClick = {
                    if (prompt.isNotBlank()) {
                        viewModel.askCosmicOracle(prompt)
                        prompt = ""
                    }
                },
                enabled = prompt.isNotBlank() && !isLoading,
                modifier = Modifier
                    .size(52.dp)
                    .testTag("ask_oracle_submit_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NebulaViolet,
                    disabledContainerColor = CosmicSurface
                ),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Query AI",
                    tint = if (prompt.isNotBlank()) Color.White else Color.White.copy(alpha = 0.3f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
