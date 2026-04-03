package com.example.onlineexaminationsystem.ui.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import com.example.onlineexaminationsystem.ui.teacher.TeacherDashboardScreen
import com.example.onlineexaminationsystem.ui.topstudent.TopStudentsScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = Screen.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {



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
                    navController.navigate(Screen.Profile.route) }


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