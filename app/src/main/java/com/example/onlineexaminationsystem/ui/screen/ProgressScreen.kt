package com.example.onlineexaminationsystem.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

import com.example.onlineexaminationsystem.data.model.Status
import com.example.onlineexaminationsystem.data.model.SubmittedExam
import com.example.onlineexaminationsystem.ui.theme.ErrorRed
import com.example.onlineexaminationsystem.ui.theme.SuccessGreen
import com.example.onlineexaminationsystem.ui.viewmodel.ProgressViewModel

@Composable
fun ProgressScreen(
    viewModel: ProgressViewModel = hiltViewModel(),
    onExamClick:(SubmittedExam)->Unit
) {
    val state by viewModel.uiState.collectAsState()

    if (state.isLoading) {
        CircularProgressIndicator()
    } else if (state.error != null) {
        Text("Error: ${state.error}")
    }else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Your Progress",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            if (state.examHistory.size == 0) {
                Text(
                    text = "You haven't take any exam yet",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }


            Spacer(modifier = Modifier.height(24.dp))

            ExamHistoryList(
                state.examHistory,
                state.examNames,
                viewModel::dateFormated,
                onExamClick
            )
        }
    }

}

@Composable
fun ExamHistoryList(
    examHistory: List<SubmittedExam>,
    examNames: Map<Long, String>,
    date:(Long)->String,
   onClick: (SubmittedExam) -> Unit
) {
    LazyColumn {
        items(examHistory) { exam ->
            val name = examNames[exam.examId]?:"Unknown"

            ExamCard(
                examName =name ,
                result = exam,
                date = date(exam.date),
                onClick = { onClick(exam) }
            )
        }
    }

}

@Composable
fun ExamCard(
    examName: String,
    result:SubmittedExam,
    onClick: () -> Unit ,
    date:String
) {
    val statusColor =
        if (result.status == Status.PASSED) SuccessGreen else ErrorRed

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        onClick = onClick

    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column(modifier = Modifier.weight(1f)) {

                Text(
                    text = examName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Score: ${result.score} ",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Grade: ${result.grade}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Date: ${date}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }


            Box(
                modifier = Modifier
                    .size(55.dp)
                    .clip(CircleShape)
                    .background(statusColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = result.status.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}
