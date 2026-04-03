package com.example.onlineexaminationsystem.data.remote

data class AnswerSnapshotDto (
    val questionText: String = "",
    val options: List<String> = emptyList(),
    val correctAnswerIndex: Int = 0,
    val examMark: Int = 0,
    val studentAnswerIndex: Int = 0
)
