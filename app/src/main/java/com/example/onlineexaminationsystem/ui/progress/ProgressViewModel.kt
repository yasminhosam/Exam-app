package com.example.onlineexaminationsystem.ui.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlineexaminationsystem.domain.model.SubmittedExam
import com.example.onlineexaminationsystem.domain.repository.AuthRepository
import com.example.onlineexaminationsystem.domain.repository.ExamRepository
import com.example.onlineexaminationsystem.domain.repository.StudentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ProgressViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val studentRepository: StudentRepository,
    private val examRepository: ExamRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProgressUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadProgress()
    }

    private fun loadProgress() {
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentSession()

            if (currentUser == null) {
                _uiState.update {
                    it.copy(error = "User not logged in")
                }
                return@launch
            }
            combine(
                studentRepository.getExamHistory(currentUser.id),
                examRepository.getAllExams()
            ) { history, exams ->
                val namesMap = exams.associate {
                    it.exam.id to it.exam.title
                }
                ProgressUiState(
                    isLoading = false,
                    examHistory = history,
                    examNames = namesMap

                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }


    private val dateFormat = SimpleDateFormat("dd MMM yyyy • HH:mm", Locale.getDefault())
    fun dateFormatted(time: Long): String = dateFormat.format(Date(time))

}

data class ProgressUiState(
    val isLoading: Boolean = false,
    val examHistory: List<SubmittedExam> = emptyList(),
    val examNames: Map<String, String> = emptyMap(),
    val error: String? = null
)