package com.example.onlineexaminationsystem.ui.exam

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

private val CorrectGreen = Color(0xFF2E7D32)
private val CorrectGreenBg = Color(0xFFE8F5E9)
private val WrongRed = Color(0xFFC62828)
private val WrongRedBg = Color(0xFFFFEBEE)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewExamScreen(
    viewModel: ReviewExamViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val snapshot = state.currentSnapshot
    val total = state.snapshots.size

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Review Answers",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleMedium
                        )
                        if (total > 0) {
                            Text(
                                "Question ${state.currentIndex + 1} of $total",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->

        when {
            state.isLoading -> {
                Box(
                    Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }

            state.snapshots.isEmpty() -> {
                Box(
                    Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No answers to review",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            snapshot != null -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp)
                ) {

                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { (state.currentIndex + 1).toFloat() / total },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))


                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        state.snapshots.forEachIndexed { i, snap ->
                            val isActive = i == state.currentIndex
                            val isCorrect = snap.studentAnswerIndex == snap.correctAnswerIndex
                            val dotColor = when {
                                isActive -> MaterialTheme.colorScheme.primary
                                isCorrect -> CorrectGreen
                                snap.studentAnswerIndex == -1 -> MaterialTheme.colorScheme.outline
                                else -> WrongRed
                            }
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 3.dp)
                                    .size(if (isActive) 10.dp else 8.dp)
                                    .clip(CircleShape)
                                    .background(dotColor)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))


                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                    ) {


                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            elevation = CardDefaults.cardElevation(0.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text(
                                    text = "Q${state.currentIndex + 1}",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = snapshot.questionText,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    lineHeight = 26.sp
                                )
                                // Mark worth
                                Spacer(modifier = Modifier.height(10.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "${snapshot.examMark} mark${if (snapshot.examMark != 1) "s" else ""}",
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        // Unanswered warning
                        if (snapshot.studentAnswerIndex == -1) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(WrongRedBg)
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = null,
                                    tint = WrongRed,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    "This question was not answered",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = WrongRed
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Options
                        snapshot.options.forEachIndexed { index, optionText ->
                            val isCorrect = index == snapshot.correctAnswerIndex
                            val isStudentChoice = index == snapshot.studentAnswerIndex
                            val isWrongChoice = isStudentChoice && !isCorrect

                            val bgColor = when {
                                isCorrect -> CorrectGreenBg
                                isWrongChoice -> WrongRedBg
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }
                            val borderColor = when {
                                isCorrect -> CorrectGreen
                                isWrongChoice -> WrongRed
                                else -> Color.Transparent
                            }
                            val labelColor = when {
                                isCorrect -> CorrectGreen
                                isWrongChoice -> WrongRed
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            }

                            val optionLabel = listOf("A", "B", "C", "D").getOrElse(index) { "${index + 1}" }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 5.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(bgColor)
                                    .border(
                                        width = if (borderColor != Color.Transparent) 1.5.dp else 0.dp,
                                        color = borderColor,
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Option letter badge
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .clip(CircleShape)
                                        .background(labelColor.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = optionLabel,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = labelColor
                                    )
                                }

                                Text(
                                    text = optionText,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )


                                when {
                                    isCorrect -> Icon(
                                        Icons.Default.Check,
                                        contentDescription = "Correct",
                                        tint = CorrectGreen,
                                        modifier = Modifier.size(18.dp)
                                    )
                                    isWrongChoice -> Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Wrong",
                                        tint = WrongRed,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // ── Navigation ─────────────────────────────────────────────
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Previous
                        FilledTonalButton(
                            onClick = viewModel::onPreviousClick,
                            enabled = state.currentIndex > 0,
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Prev")
                        }

                        // Page indicator
                        Text(
                            text = "${state.currentIndex + 1} / $total",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Next / Done
                        if (state.currentIndex == total - 1) {
                            Button(
                                onClick = onBack,
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Done")
                            }
                        } else {
                            Button(
                                onClick = viewModel::onNextClick,
                                shape = RoundedCornerShape(14.dp)
                            ) {
                                Text("Next")
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}