package com.example.onlineexaminationsystem.ui.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlineexaminationsystem.domain.model.Category
import com.example.onlineexaminationsystem.domain.repository.AuthRepository
import com.example.onlineexaminationsystem.domain.repository.ExamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val examRepository: ExamRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val uiState:StateFlow<StudentMainState> =examRepository.getAllCategories()
        .map{categories ->
            StudentMainState(
                categories = categories,
                isLoading = false,
                username = authRepository.getCurrentSession()?.name?:"User"
            )

        }
        .catch { e ->
            emit(StudentMainState(isLoading = false, error = e.message))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = StudentMainState(isLoading = true)
        )


}
    data class StudentMainState(
        val isLoading: Boolean = false,
        val categories: List<Category> = emptyList(),
        val error: String? = null,
        val username: String = ""
    )
