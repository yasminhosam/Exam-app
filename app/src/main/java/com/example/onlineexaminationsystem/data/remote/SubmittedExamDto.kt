package com.example.onlineexaminationsystem.data.remote

import com.example.onlineexaminationsystem.domain.model.Status
import java.util.UUID.randomUUID

data class SubmittedExamDto(

    val id:String= "",
    val examId:String="",

    val studentId:String="",
    val studentName:String="",

    var score:Int=0,
    val grade:String="",
    val status: String="",
    val date: Long=0L,
    val snapshots:List<AnswerSnapshotDto>
)
