package com.example.onlineexaminationsystem.ui.navigation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.onlineexaminationsystem.ui.screen.ExamListScreen
import com.example.onlineexaminationsystem.ui.screen.ExamScreen
import com.example.onlineexaminationsystem.ui.screen.HomeScreen
import com.example.onlineexaminationsystem.ui.screen.LoginScreen
import com.example.onlineexaminationsystem.ui.screen.ProfileScreen
import com.example.onlineexaminationsystem.ui.screen.ProgressScreen
import com.example.onlineexaminationsystem.ui.screen.RegisterScreen
import com.example.onlineexaminationsystem.ui.screen.ReviewExamScreen
import com.example.onlineexaminationsystem.ui.viewmodel.AuthViewModel
import com.example.onlineexaminationsystem.ui.viewmodel.ExamViewModel
import com.example.onlineexaminationsystem.ui.viewmodel.MainViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier=Modifier
    ) {

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route,
        modifier = modifier
    ) {

        composable(Screen.Home.route) {
            HomeScreen(
                onCategoryClick = {category->
                    Log.d("debug","clicked on ${category.name}")
                    val route=Screen.ExamList.createRoute(category.id)
                    navController.navigate(route)
                },
                onProfileClick = {
                    navController.navigate(Screen.Profile.route)
                },
                onProgressClick = {navController.navigate(Screen.Progress.route)}
            )
        }
        composable(Screen.Profile.route){
            ProfileScreen(
                onBack = { navController.popBackStack() },
                onLogout = {

                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true } // Clears everything
                    }
                }
            )
        }
        composable(
            route=Screen.ExamList.route,
            arguments = listOf(
                navArgument("categoryId"){type= NavType.LongType}
            )
        ) {
            ExamListScreen(
                 onBakClick={navController.popBackStack()},
                onExamClick={examWithDetails ->
                    val route=Screen.Exam.createRoute(examWithDetails.exam.id)
                    navController.navigate(route)}
            )
        }
        composable(
           route= Screen.Exam.route,
            arguments= listOf(
                navArgument("id"){type= NavType.LongType}
            )
        ) {
                ExamScreen(
                    onExamFinished = { navController.popBackStack() }
                )
            }

        composable(Screen.Login.route) {

            LoginScreen(
                onNavigateToRegister = {navController.navigate(Screen.Register.route)},
                onLoginSuccessStudent = {
                    navController.navigate(Screen.Home.route){
                        popUpTo(Screen.Login.route){inclusive=true }
                    }
                },
                onLoginSuccessAdmin = {
                    navController.navigate(Screen.Home.route){
                        popUpTo(Screen.Login.route){inclusive=true }
                    }
                }
            )
        }
        composable(Screen.Register.route) {

            RegisterScreen(
                onNavigateToLogin = {navController.popBackStack()},
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route){
                        popUpTo(Screen.Login.route){inclusive=true}
                    }
                }
            )
        }
        composable(Screen.Progress.route) {

            ProgressScreen(
                onExamClick = { submittedExam->
                    val route =Screen.ReviewExam.createRoute(submittedExam.id)
                    navController.navigate(route)
                }
            )
        }
        composable(
            route=Screen.ReviewExam.route,
            arguments = listOf(
                navArgument("examId"){type=NavType.LongType}
            )
        ) { ReviewExamScreen(
            onBack = {
                navController.popBackStack()
            }
        ) }

    }
}