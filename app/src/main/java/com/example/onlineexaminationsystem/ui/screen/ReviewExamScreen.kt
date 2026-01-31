package com.example.onlineexaminationsystem.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.onlineexaminationsystem.data.model.SubmittedExam
import com.example.onlineexaminationsystem.ui.theme.BluePrimary
import com.example.onlineexaminationsystem.ui.theme.ErrorRed
import com.example.onlineexaminationsystem.ui.theme.SuccessGreen
import com.example.onlineexaminationsystem.ui.viewmodel.ReviewExamViewModel

@Composable
fun ReviewExamScreen(
    viewModel: ReviewExamViewModel= hiltViewModel(),
    onBack:()->Unit
){
    val state=viewModel.uiState.collectAsState()
    val snapshot = state.value.currentSnapshot
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(58.dp))
        if(snapshot==null){
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }else{
            Card(
                colors = CardDefaults.cardColors(BluePrimary.copy(alpha = 0.08f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .align(Alignment.CenterHorizontally)

            ) {
                Box(
                    modifier = Modifier.padding(20.dp),

                    ) {
                    Text(
                        text = snapshot.questionText,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )

                }
            }
            if(snapshot.studentAnswerIndex==-1){
                Box(modifier = Modifier.padding(8.dp)){
                    Text(
                        text = "You haven't answered this question",
                        color = MaterialTheme.colorScheme.error,
                        textAlign =TextAlign.Center
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Column {
                snapshot.options.forEachIndexed { index, s ->
                    val isCorrect=(index== snapshot.correctAnswerIndex)
                    val isSelected=(index== snapshot.studentAnswerIndex)

                    val cardColor = when {
                         isCorrect -> SuccessGreen.copy(alpha = 0.2f)
                        isSelected && !isCorrect -> ErrorRed.copy(alpha = 0.2f)
                        else ->  MaterialTheme.colorScheme.surface
                    }
                    val borderColor = when {
                        isCorrect -> SuccessGreen
                       isSelected && !isCorrect -> ErrorRed
                        else -> Color.Transparent
                    }
                    OptionCard(
                        text = s,
                        backgroundColor = cardColor,
                        borderColor = borderColor,

                        onClick = {/*do nothing*/}
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            ExamNavigationBar(
                currentQuestionIndex = state.value.currentIndex,
                onNext = { viewModel.onNextClick() },
                onPrevious = { viewModel.onPreviousClick() },
                onFinish = {onBack()},
                isLast = state.value.currentIndex == state.value.snapshots.size - 1

                )
        }
    }

}