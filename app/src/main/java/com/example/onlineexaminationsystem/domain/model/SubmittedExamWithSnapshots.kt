package com.example.onlineexaminationsystem.domain.model

import androidx.room.Embedded
import androidx.room.Relation

data class SubmittedExamWithSnapshots(
    @Embedded
    val submission:SubmittedExam,
    @Relation(
        parentColumn = "id",
        entityColumn = "submitted_exam_id"
    )
    val snapshots:List<AnswerSnapshot>
)
