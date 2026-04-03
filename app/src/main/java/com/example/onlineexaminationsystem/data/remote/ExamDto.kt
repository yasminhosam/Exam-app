package com.example.onlineexaminationsystem.data.remote

data class ExamDto(
    val id: String = "",
    val categoryId: String = "",
    val teacherId:String ="",
    val title: String = "",
    val durationMillis: Long = 0L,
    val passPercentage: Int = 0,
    val totalScore: Int = 0,
    val questions: List<QuestionDto> = emptyList()
)
