package com.example.onlineexaminationsystem.domain.model

import androidx.room.Embedded
import androidx.room.Relation

data class ExamWithDetails (
    @Embedded val exam: Exam,
    @Relation(
        parentColumn ="id",
        entityColumn = "exam_id"
    )
    val questions: List<Question>,

    @Relation(
        parentColumn = "category_id",
        entityColumn = "id"
    )
    val category: Category
)