package com.example.onlineexaminationsystem.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlineexaminationsystem.data.model.SubmittedExam
import com.example.onlineexaminationsystem.data.repository.AuthRepository
import com.example.onlineexaminationsystem.data.repository.ExamRepository
import com.example.onlineexaminationsystem.data.repository.StudentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val examRepository: ExamRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProgressUiState())
    val uiState=_uiState.asStateFlow()
    init {
        loadProgress()
    }
   private  fun loadProgress(){
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val currentUser = authRepository.currentUser.value
            if (currentUser!=null){
                try {
                    val history=studentRepository.getExamHistory(studentId = currentUser.id)
                    val allExams = examRepository.getAllExams()

                    val namesMap= emptyMap<Long, String>().toMutableMap()
                    allExams.forEach { e->
                        namesMap[e.exam.id] = e.exam.title
                    }
                    _uiState.update { it.copy(
                        isLoading = false,
                        examHistory = history,
                        examNames = namesMap
                    ) }
                }catch (e:Exception){
                    _uiState.update {
                        it.copy(isLoading = false, error = "Failed to load history: ${e.message}")
                    }
                }
            }else{
                _uiState.update {
                    it.copy(isLoading = false, error = "User not logged in")
                }
            }
        }
    }


     private  val dateFormat = SimpleDateFormat("dd/MM/yyyy  HH:mm", Locale.getDefault())
    fun dateFormated(time: Long): String {
        return dateFormat.format(Date(time))
    }


}
data class ProgressUiState(
    val isLoading: Boolean = false,
    val examHistory: List<SubmittedExam> = emptyList(),
    val examNames: Map<Long, String> = emptyMap(),
    val error: String? = null
)