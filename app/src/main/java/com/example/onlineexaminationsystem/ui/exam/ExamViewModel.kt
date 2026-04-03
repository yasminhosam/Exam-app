package com.example.onlineexaminationsystem.ui.exam

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlineexaminationsystem.data.repository.AuthRepositoryImpl
import com.example.onlineexaminationsystem.data.repository.ExamRepositoryImpl
import com.example.onlineexaminationsystem.domain.model.ExamWithDetails
import com.example.onlineexaminationsystem.data.repository.StudentRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExamViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val authRepositoryImpl: AuthRepositoryImpl,
    private val studentRepositoryImpl: StudentRepositoryImpl,
    private val examRepositoryImpl: ExamRepositoryImpl
) : ViewModel() {

    // return null if the key is missing
    private val examId= savedStateHandle.get<String>("id")
    var selectedExam by mutableStateOf<ExamWithDetails?>(null)
        private set
    init {
        viewModelScope.launch {
            if(examId!=null) {
               val fetchedExam=examRepositoryImpl.getExamById(examId)
                if (fetchedExam != null) {
                    startExam(fetchedExam)

                }
            }else{
                Log.e("ExamViewModel", "Error: examId was null or missing")
            }
        }
    }
    var currentQuestionIndex by mutableStateOf(0)
        private set

    val studentAnswers = mutableStateMapOf<String, Int>()


    var selectedOptionIndex by mutableStateOf(-1)
        private set

    var isAnswerChecked by mutableStateOf(false)
        private set

    var timeLeftSeconds by mutableStateOf(0L)

    var isExamFinished by mutableStateOf(false)
        private set
    private var timerJob: Job? = null




    fun startExam(examWithDetails: ExamWithDetails) {
        Log.d("DEBUG_TIMER","1 startExam called")
        // to prevent exam to start from zero if the user ia already taking it
        if (selectedExam?.exam?.id == examWithDetails.exam.id){
            Log.d("DEBUG_TIMER", "2. Guard clause hit! Returning early.")
            return
        }
        Log.d("DEBUG_TIMER","3 setting timer")
        selectedExam = examWithDetails
        currentQuestionIndex = 0
        timeLeftSeconds = examWithDetails.exam.duration.inWholeSeconds
        Log.d("DEBUG_TIMER", "4. Duration is: $timeLeftSeconds seconds")
        timerJob?.cancel()
        timer()

    }

    fun timer() {
        Log.d("DEBUG_TIMER","5 timer coroutine starting")
        timerJob = viewModelScope.launch {
            while (timeLeftSeconds > 0) {
                delay(1000)
                timeLeftSeconds--
                Log.d("DEBUG_TIMER", "Tick: $timeLeftSeconds")
            }
            finishExam()
        }

    }

    var finalScore=0
        private set
    fun finishExam() {
        if (isExamFinished) return
        isExamFinished = true
        timerJob?.cancel()
        // save last question's answer
        saveCurrentAnswer()

        val currentStudent = authRepositoryImpl.getCurrentSession()
        val currentExam = selectedExam

        if (currentStudent != null && currentExam != null) {
            viewModelScope.launch {
                try {
                    finalScore=0
                    currentExam.questions.forEach {q->
                        if (studentAnswers[q.id] ==q.correctAnswer)
                            finalScore+=q.mark
                    }
                    studentRepositoryImpl.submitExam(
                        studentId = currentStudent.id,
                        examWithDetails = currentExam,
                        studentName = currentStudent.name,
                        studentAnswers = studentAnswers

                    )
                } catch (e: Exception) {
                    Log.d("Exam","failed to submit")

                }
            }
        }

    }


    fun onNextClick(){
        saveCurrentAnswer()
        val totalQuestions = selectedExam?.questions?.size ?: 0
        if(currentQuestionIndex < totalQuestions-1){
            currentQuestionIndex++
            isAnswerChecked = false
            refreshSelectedOption()
        }
    }
    fun onPreviousClick(){
        saveCurrentAnswer()
        if(currentQuestionIndex>0){
            currentQuestionIndex--
            isAnswerChecked=false
            refreshSelectedOption()
        }
    }


    fun refreshSelectedOption(){
        val question=selectedExam?.questions?.getOrNull(currentQuestionIndex)
        if(question!=null){
            selectedOptionIndex=studentAnswers[question.id]?:-1
        }
    }
    private fun saveCurrentAnswer() {
    val currentQ = selectedExam?.questions?.getOrNull(currentQuestionIndex)
    if (currentQ != null) {
        studentAnswers[currentQ.id] = selectedOptionIndex
    }
}

    fun onOptionSelected(index: Int) {
        if (!isAnswerChecked)
            selectedOptionIndex = index

    }

    fun timeFormated(): String {
        var min = timeLeftSeconds / 60
        var sec = timeLeftSeconds % 60
        return "%02d:%02d".format(min, sec)
    }


}