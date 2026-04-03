package com.example.onlineexaminationsystem.domain

object GradeCalculator {
     fun calculateGradeLetter(studentScore: Int, passPercentage: Int): String {
        if (studentScore < passPercentage) return "F"
        val gap = studentScore - passPercentage
        return when {
            (gap >= 30) -> "A"
            (gap >= 20) -> "B"
            (gap >= 10) -> "C"
            else -> "D"
        }
    }
}