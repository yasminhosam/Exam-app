package com.example.onlineexaminationsystem.data.remote


data class QuestionDto(
    val text: String = "",
    val options: List<String> = emptyList(),
    val correctAnswer: Int = 0,
    val mark: Int = 0
)