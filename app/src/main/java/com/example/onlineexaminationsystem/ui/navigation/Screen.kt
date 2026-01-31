package com.example.onlineexaminationsystem.ui.navigation

sealed class Screen(val route :String) {
    object Login: Screen("login")
    object Register: Screen("register")
    object Home: Screen("home")
    object ExamList: Screen("exam_list/{categoryId}"){
        fun createRoute(categoryId:Long)="exam_list/${categoryId}"
    }
    object Exam: Screen("exam/{id}"){
        fun createRoute(id:Long)="exam/${id}"
    }
    object Progress: Screen("progress")
    object Profile:Screen("profile")
    object ReviewExam:Screen("review_exam/{examId}"){
        fun createRoute(submissionId:Long)="review_exam/${submissionId}"
    }


}