package com.example.onlineexaminationsystem.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object ExamList : Screen("exam_list/{categoryId}") {
        fun createRoute(categoryId: String) = "exam_list/$categoryId"
    }
    object Exam : Screen("exam/{id}") {
        fun createRoute(id: String) = "exam/$id"
    }
    object Progress : Screen("progress")
    object Profile : Screen("profile")
    object ReviewExam : Screen("review_exam/{examId}") {
        fun createRoute(submissionId: String) = "review_exam/$submissionId"
    }

    object TeacherDashboard : Screen("teacher_dashboard")
    object CreateExam : Screen("create_exam")
    object TopStudents : Screen("top_students")
}