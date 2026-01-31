package com.example.onlineexaminationsystem.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.onlineexaminationsystem.data.model.Category
import com.example.onlineexaminationsystem.data.model.Exam
import com.example.onlineexaminationsystem.data.model.ExamWithDetails
import com.example.onlineexaminationsystem.ui.theme.SuccessGreen
import com.example.onlineexaminationsystem.ui.theme.TextSecondary
import com.example.onlineexaminationsystem.ui.viewmodel.ExamListViewModel
import com.example.onlineexaminationsystem.ui.viewmodel.MainViewModel
import kotlin.time.Duration.Companion.minutes

@Composable
fun ExamListScreen(
    viewModel: ExamListViewModel = hiltViewModel(),
    onExamClick: (ExamWithDetails) -> Unit,
    onBakClick: () -> Unit
) {

    val state by viewModel.uiState.collectAsState()


    if (state.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (state.error != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = "Error: ${state.error}", color = Color.Red)
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onBakClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "back")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${state.categoryName} Exams",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            if (state.exams.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No exams found for this category.")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)

                ) {
                    items(state.exams) { exam ->
                        ExamCard(exam, { onExamClick(exam) })

                    }
                }

            }
        }
    }

}

@Composable
fun ExamCard(examWithDetails: ExamWithDetails, onClick: () -> Unit) {

    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${examWithDetails.exam.title}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "${examWithDetails.exam.duration.inWholeMinutes} mins  ${examWithDetails.exam.totalScore} Marks",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

            }
            Spacer(modifier = Modifier.weight(0.1f))
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Start",
                tint = SuccessGreen,
                modifier = Modifier
                    .padding(4.dp)
                    .size(33.dp)
            )
        }
    }

}


@Preview(showBackground = true)
@Composable
fun ExamListPreview() {


}