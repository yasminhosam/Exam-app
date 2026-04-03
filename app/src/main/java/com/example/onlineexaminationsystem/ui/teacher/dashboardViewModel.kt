package com.example.onlineexaminationsystem.ui.teacher

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlineexaminationsystem.domain.model.ExamWithDetails
import com.example.onlineexaminationsystem.domain.model.User
import com.example.onlineexaminationsystem.domain.repository.AuthRepository
import com.example.onlineexaminationsystem.domain.repository.ExamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TeacherDashboardViewModel @Inject constructor(
    private val examRepository: ExamRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TeacherDashboardUiState(
        teacherName = authRepository.getCurrentSession()?.name?:"User"
    ))
    val uiState = _uiState.asStateFlow()

    private val teacherId=authRepository.getCurrentSession()?.id

    init {
        loadExams()
    }

    private fun loadExams() {
        val currentTeacherId=teacherId
        if (currentTeacherId == null) {
            _uiState.update { it.copy(error = "Session expired. Please log in again.") }
            return
        }

        examRepository.getExamsByTeacher(currentTeacherId)
            .onEach { exams ->
                _uiState.update { it.copy(exams = exams, isLoading = false, error = null) }
            }
            .catch { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Failed to load exams") }
            }
            .launchIn(viewModelScope)
    }

    fun deleteExam(examId: String) {
        viewModelScope.launch {
            try {
                examRepository.deleteExam(examId)
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Failed to delete exam") }
            }
        }
    }

    fun dismissError() = _uiState.update { it.copy(error = null) }
}

data class TeacherDashboardUiState(
    val teacherName: String = "",
    val exams: List<ExamWithDetails> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)