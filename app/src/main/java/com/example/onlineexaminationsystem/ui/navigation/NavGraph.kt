package com.example.onlineexaminationsystem.ui.navigation

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.onlineexaminationsystem.ui.exam.CreateExamScreen
import com.example.onlineexaminationsystem.ui.exam.ExamListScreen
import com.example.onlineexaminationsystem.ui.exam.ExamScreen
import com.example.onlineexaminationsystem.ui.student.HomeScreen
import com.example.onlineexaminationsystem.ui.auth.LoginScreen
import com.example.onlineexaminationsystem.ui.profile.ProfileScreen
import com.example.onlineexaminationsystem.ui.progress.ProgressScreen
import com.example.onlineexaminationsystem.ui.auth.RegisterScreen
import com.example.onlineexaminationsystem.ui.exam.ReviewExamScreen
import com.example.onlineexaminationsystem.ui.splash.SplashScreen
import com.example.onlineexaminationsystem.ui.sync.SyncState
import com.example.onlineexaminationsystem.ui.sync.SyncViewModel
import com.example.onlineexaminationsystem.ui.teacher.TeacherDashboardScreen
import com.example.onlineexaminationsystem.ui.topstudent.TopStudentsScreen
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.Login.route,
    syncViewModel: SyncViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }

    val syncState by syncViewModel.syncState.collectAsState()
    val scope =rememberCoroutineScope()

    LaunchedEffect(syncState) {
        val message = when (syncState) {
            is SyncState.Queued -> "Waiting for network connection..."
            is SyncState.Syncing -> "Syncing..."
            is SyncState.Success -> "Synced Successfully"
            is SyncState.Failed -> "Sync Failed"
            is SyncState.Idle -> null
        }
        message?.let {
            snackbarHostState.currentSnackbarData?.dismiss()
            scope.launch {

                snackbarHostState.showSnackbar(it)
            }
            syncViewModel.consumeSyncState()
        }
    }
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = modifier
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(
                    onNavigateToLogin = { navController.navigate(Screen.Login.route) {
                        popUpTo(0)
                    } },
                    onNavigateToStudentHome = {navController.navigate(Screen.Home.route){popUpTo(0)} },
                    onNavigateToTeacherDashboard = {navController.navigate(Screen.TeacherDashboard.route){popUpTo(0)} },
                )
            }


            composable(Screen.Login.route) {
                LoginScreen(
                    onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                    onLoginSuccessStudent = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onLoginSuccessTeacher = {

                        navController.navigate(Screen.TeacherDashboard.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Register.route) {
                RegisterScreen(
                    onNavigateToLogin = { navController.popBackStack() },
                    onRegisterStudentSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    },
                    onRegisterTeacherSuccess = {
                        navController.navigate(Screen.TeacherDashboard.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                )
            }



            composable(Screen.Home.route) {
                HomeScreen(
                    onCategoryClick = { category ->
                        Log.d("debug", "clicked on ${category.name}")
                        navController.navigate(Screen.ExamList.createRoute(category.id))
                    },
                    onProfileClick = { navController.navigate(Screen.Profile.route) },
                    onProgressClick = { navController.navigate(Screen.Progress.route) }
                )
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    onBack = { navController.popBackStack() },
                    onLogout = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(
                route = Screen.ExamList.route,
                arguments = listOf(navArgument("categoryId") { type = NavType.StringType })
            ) {
                ExamListScreen(
                    onBakClick = { navController.popBackStack() },
                    onExamClick = { examWithDetails ->
                        navController.navigate(Screen.Exam.createRoute(examWithDetails.exam.id))
                    }
                )
            }

            composable(
                route = Screen.Exam.route,
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) {
                ExamScreen(onExamFinished = { navController.popBackStack() })
            }

            composable(Screen.Progress.route) {
                ProgressScreen(
                    onExamClick = { submittedExam ->
                        navController.navigate(Screen.ReviewExam.createRoute(submittedExam.id))
                    }
                )
            }

            composable(
                route = Screen.ReviewExam.route,
                arguments = listOf(navArgument("examId") { type = NavType.StringType })
            ) {
                ReviewExamScreen(onBack = { navController.popBackStack() })
            }



            composable(Screen.TeacherDashboard.route) {
                TeacherDashboardScreen(
                    onNavigateToCreateExam = { navController.navigate(Screen.CreateExam.route) },
                    onNavigateToTopStudents = { navController.navigate(Screen.TopStudents.route) },
                    onProfileClick = {
                        navController.navigate(Screen.Profile.route)
                    }


                )
            }

            composable(Screen.CreateExam.route) {
                CreateExamScreen(
                    onExamCreated = { navController.popBackStack() },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.TopStudents.route) {
                TopStudentsScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}
