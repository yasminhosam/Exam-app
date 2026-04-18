package com.example.onlineexaminationsystem.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.onlineexaminationsystem.R
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToStudentHome: () -> Unit,
    onNavigateToTeacherDashboard: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val event by viewModel.navigationEvent.collectAsState()

    val scale = remember { Animatable(0.7f) }
    val alpha = remember { Animatable(0f) }

    var isAnimationFinished by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        launch {
            scale.animateTo(1f, animationSpec = tween(600))
        }
        launch {
            alpha.animateTo(1f, animationSpec = tween(600))
        }

        kotlinx.coroutines.delay(1200)
        isAnimationFinished = true
    }


    LaunchedEffect(event, isAnimationFinished) {
        if (isAnimationFinished) {
            when (event) {
                is SplashEvent.NavigateToLogin -> onNavigateToLogin()
                is SplashEvent.NavigateToStudent -> onNavigateToStudentHome()
                is SplashEvent.NavigateToTeacher -> onNavigateToTeacherDashboard()
                SplashEvent.Loading -> {} // Still fetching data, wait here
            }
        }
    }


    val contentColor = MaterialTheme.colorScheme.onPrimary

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .scale(scale.value)
                .alpha(alpha.value)
        ) {
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(24.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_launcher_foreground),
                    contentDescription = "Prova logo",
                    modifier = Modifier.size(88.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "PROVA",
                color = contentColor,
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp
            )

            Text(
                text = "Exam · Assess · Achieve",
                color = contentColor.copy(alpha = 0.75f),
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))


            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(2.dp)
                    .background(
                        color = Color(0xFF14B8A6),
                        shape = RoundedCornerShape(1.dp)
                    )
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 52.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(3) { index ->
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(
                            color = if (index == 1) contentColor else contentColor.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(3.dp)
                        )
                )
            }
        }
    }
}