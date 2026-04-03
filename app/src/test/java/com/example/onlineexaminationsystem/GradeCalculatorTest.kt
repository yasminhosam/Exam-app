package com.example.onlineexaminationsystem

import com.example.onlineexaminationsystem.domain.GradeCalculator
import org.junit.Test
import org.junit.Assert.assertEquals

class GradeCalculatorTest {
    @Test
    fun `score 30  or more points above pass percentage returns A`(){
        val result =GradeCalculator.calculateGradeLetter(80,50)
        assertEquals('A',result)

    }

    @Test
    fun `score below pass percentage returns F`(){
        val result =GradeCalculator.calculateGradeLetter(40,50)
        assertEquals('F',result)

    }

    @Test
    fun `score exactly at pass percentage returns D`(){
        val result =GradeCalculator.calculateGradeLetter(50,50)
        assertEquals('D',result)

    }
    @Test
    fun `score 10 points above pass percentage returns C`(){
        val result =GradeCalculator.calculateGradeLetter(60,50)
        assertEquals('C',result)

    }
    @Test
    fun `score 20 points above pass percentage returns B`(){
        val result =GradeCalculator.calculateGradeLetter(70,50)
        assertEquals('B',result)


    }
}