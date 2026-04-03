package com.example.onlineexaminationsystem.ui.topstudent

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.onlineexaminationsystem.domain.model.TopStudent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopStudentsScreen(
    viewModel: TopStudentsViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Top Students 🏆", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::loadTopStudents) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { padding ->

        when {
            state.isLoading -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            state.error != null -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.ErrorOutline, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.error)
                        Text(state.error!!, color = MaterialTheme.colorScheme.error)
                        Button(onClick = viewModel::loadTopStudents) { Text("Retry") }
                    }
                }
            }

            state.topStudents.isEmpty() -> {
                Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.EmojiEvents,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        )
                        Text("No results yet", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Students haven't submitted any exams yet.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
                    }
                }
            }

            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    // ── Podium (top 3) ─────────────────────────────────────
                    if (state.topStudents.size >= 3) {
                        item {
                            PodiumRow(top3 = state.topStudents.take(3))
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }

                    // ── Full leaderboard list ──────────────────────────────
                    itemsIndexed(state.topStudents) { index, student ->
                        LeaderboardRow(rank = index + 1, student = student)
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

// ─── Podium ───────────────────────────────────────────────────────────────────

@Composable
private fun PodiumRow(top3: List<TopStudent>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            PodiumItem(student = top3[1], rank = 2, barHeight = 80.dp, medalColor = Color(0xFFB0BEC5))
            PodiumItem(student = top3[0], rank = 1, barHeight = 110.dp, medalColor = Color(0xFFFFD700))
            PodiumItem(student = top3[2], rank = 3, barHeight = 60.dp, medalColor = Color(0xFFCD7F32))
        }
    }
}

@Composable
private fun PodiumItem(
    student: TopStudent,
    rank: Int,
    barHeight: androidx.compose.ui.unit.Dp,
    medalColor: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.height(barHeight + 80.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        Text(
            text = student.name.take(10),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = "${student.score} pts",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )

        Text(
            text = "Grade ${student.grade}",
            style = MaterialTheme.typography.labelSmall,
            color = gradeColor(student.grade)
        )
        Spacer(modifier = Modifier.height(4.dp))

        Surface(
            modifier = Modifier.size(44.dp),
            shape = CircleShape,
            color = medalColor.copy(alpha = 0.25f)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Text(
                    text = when (rank) { 1 -> "🥇"; 2 -> "🥈"; else -> "🥉" },
                    fontSize = 22.sp
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        // Podium bar
        Surface(
            modifier = Modifier
                .width(72.dp)
                .height(barHeight),
            shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
            color = medalColor.copy(alpha = 0.35f)
        ) {}
    }
}


@Composable
private fun LeaderboardRow(rank: Int, student: TopStudent) {
    val isTopThree = rank <= 3
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isTopThree)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            else
                MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Surface(
                modifier = Modifier.size(36.dp),
                shape = CircleShape,
                color = when (rank) {
                    1 -> Color(0xFFFFD700).copy(alpha = 0.25f)
                    2 -> Color(0xFFB0BEC5).copy(alpha = 0.25f)
                    3 -> Color(0xFFCD7F32).copy(alpha = 0.25f)
                    else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                }
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = if (rank <= 3) when (rank) { 1 -> "🥇"; 2 -> "🥈"; else -> "🥉" } else "#$rank",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))


            Surface(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = student.name.firstOrNull()?.uppercase() ?: "?",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))


            Text(
                text = student.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(1f)
            )

            // Score + Grade — both come from TopStudent model
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${student.score} pts",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Grade ${student.grade}",
                    style = MaterialTheme.typography.labelSmall,
                    color = gradeColor(student.grade),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}


private fun gradeColor(grade: Char): Color = when (grade) {
    'A' -> Color(0xFF4CAF50)
    'B' -> Color(0xFF2196F3)
    'C' -> Color(0xFFFFC107)
    'D' -> Color(0xFFFF9800)
    else -> Color(0xFFF44336)
}