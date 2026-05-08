package com.example.onlineexaminationsystem.domain.repository

import com.example.onlineexaminationsystem.data.remote.QuestionDto
import com.example.onlineexaminationsystem.domain.util.Resource

interface SmartGenerationRepository {
    suspend fun generateQuestionsForTopic(topic: String): Resource<List<QuestionDto>>
}