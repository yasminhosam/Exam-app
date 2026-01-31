package com.example.onlineexaminationsystem.ui.screen

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.indicatorColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.onlineexaminationsystem.data.model.Exam
import com.example.onlineexaminationsystem.data.model.ExamWithDetails
import com.example.onlineexaminationsystem.ui.theme.BluePrimary
import com.example.onlineexaminationsystem.ui.theme.ErrorRed
import com.example.onlineexaminationsystem.ui.theme.SuccessGreen
import com.example.onlineexaminationsystem.ui.theme.WarningOrange
import com.example.onlineexaminationsystem.ui.viewmodel.ExamViewModel

@Composable
fun ExamScreen(
    viewModel: ExamViewModel= hiltViewModel(),
    onExamFinished:()->Unit

) {

    val exam =viewModel.selectedExam
    if(exam==null){
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
            CircularProgressIndicator()
        }
        return
    }

    if (viewModel.isExamFinished) {
        AlertDialog(
            onDismissRequest = {onExamFinished()},
            title = { Text("Exam Finished") },
            text = { Text("Your score is ${viewModel.finalScore} / ${exam.exam.totalScore}") },
            confirmButton = {
                Button(
                onClick = {onExamFinished()}) {
                    Text("Exit")

                }
            }
        )
    }
    val question = exam.questions.getOrNull(viewModel.currentQuestionIndex)
    val totalTime = exam.exam.duration.inWholeSeconds.toFloat()
    val timeLeft = viewModel.timeLeftSeconds.toFloat()
    val progress = if (totalTime > 0) timeLeft / totalTime else 0f
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),

        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(34.dp))
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(100.dp)
        ) {
            CircularProgressIndicator(
                progress = { progress },
                modifier =Modifier.fillMaxSize(),
                strokeWidth = 6.dp,
                color = when {
                    progress < 0.2f -> ErrorRed
                    progress < 0.5f -> WarningOrange
                    else -> SuccessGreen
                }
            )
            Text(
                text = viewModel.timeFormated(),
                fontSize = 20.sp,
                modifier = Modifier.padding(12.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))


        if (question != null) {

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
                        text = question.text,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )

                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Column {
                question.options.forEachIndexed { index, s ->
                    val isSelected = (index == viewModel.selectedOptionIndex)
                    val cardColor = when {
                        isSelected ->  MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        else ->  MaterialTheme.colorScheme.surface
                    }
                    val borderColor = when {
                        isSelected ->MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.surface
                    }
                    OptionCard(
                        text = s,
                        backgroundColor = cardColor,
                        borderColor = borderColor,

                        onClick = { viewModel.onOptionSelected(index) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            ExamNavigationBar(
                currentQuestionIndex = viewModel.currentQuestionIndex,
                onNext = { viewModel.onNextClick() },
                onPrevious = { viewModel.onPreviousClick() },
                onFinish = {viewModel.finishExam()},
                isLast = viewModel.currentQuestionIndex == exam.questions.size-1,


            )

        }

    }
}
@Composable
fun ExamNavigationBar(
    currentQuestionIndex: Int,
    isLast:Boolean,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onFinish: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        // Previous Button
        OutlinedButton(
            onClick = onPrevious,
            enabled = currentQuestionIndex > 0,
            modifier = Modifier.height(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Previous question"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Previous")
        }

        // Next / Finish Button
        Button(
            onClick = {
                if (isLast) {
                    onFinish()
                } else {
                    onNext()
                }
            },
            modifier = Modifier.height(48.dp)
        ) {
            Text(
                text = if (isLast)
                    "Finish Exam"
                else
                    "Next"
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Next question"
            )
        }
    }
}

@Composable
fun OptionCard(
    text: String,
    backgroundColor: Color,
    borderColor: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            // Add a small vertical margin so cards don't touch
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        // This applies the border color passed from the parent
        border = BorderStroke(2.dp, borderColor),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        // 0.dp elevation looks cleaner with a border, but you can keep 2.dp if you prefer shadows
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), // Comfortable padding for text
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                // Optional: Make text slightly bolder to be more readable
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
//
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun OptionCard(
//    text: String,
//    backgroundColor: Color,
//    borderColor: Color,
//    onClick: () -> Unit
//) {
//    Card(
//        onClick = onClick,
//        colors = CardDefaults.cardColors(backgroundColor),
//        shape = RoundedCornerShape(12.dp),
//        //border = BorderStroke(2.dp, borderColor),
//        elevation = CardDefaults.cardElevation(2.dp),
//        modifier = Modifier
//            .fillMaxWidth()
//    ) {
//        Row(modifier = Modifier.padding(16.dp)) {
//            Box(
//                modifier = Modifier
//                    .width(4.dp)
//                    .height(24.dp)
//                    .background(indicatorColor, RoundedCornerShape(2.dp))
//            )
//
//            Spacer(modifier = Modifier.width(12.dp))
//
//
//            Text(
//                text,
//                style = MaterialTheme.typography.bodyMedium,
//
//            )
//
//        }
//
//
//    }
//
//}