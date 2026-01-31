package com.example.onlineexaminationsystem.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlineexaminationsystem.data.model.Category
import com.example.onlineexaminationsystem.data.model.Exam
import com.example.onlineexaminationsystem.data.model.ExamWithDetails
import com.example.onlineexaminationsystem.data.model.Question
import com.example.onlineexaminationsystem.data.model.User
import com.example.onlineexaminationsystem.data.repository.AuthRepository
import com.example.onlineexaminationsystem.data.repository.ExamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes

@HiltViewModel
class MainViewModel @Inject constructor(
    private val examRepository: ExamRepository,
    private val authRepository: AuthRepository
) : ViewModel() {
    val currentUser = authRepository.currentUser
    private val _uiState = MutableStateFlow(StudentMainState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                val fetchedCategories = examRepository.getAllCategories()
                _uiState.update { it.copy(isLoading = false,categories=fetchedCategories) }


            }catch (e:Exception){
                _uiState.update { it.copy(error = "Failed to load categories :${e.message}") }
            }


        }
    }

}
    data class StudentMainState(
        val isLoading: Boolean = false,
        val categories: List<Category> = emptyList(),
        val error: String? = null
    )
