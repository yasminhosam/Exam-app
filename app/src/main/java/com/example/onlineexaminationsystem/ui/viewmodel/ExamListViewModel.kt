package com.example.onlineexaminationsystem.ui.viewmodel


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlineexaminationsystem.data.model.Category
import com.example.onlineexaminationsystem.data.model.ExamWithDetails
import com.example.onlineexaminationsystem.data.repository.ExamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ExamListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val examRepository: ExamRepository
) : ViewModel() {
    private val categoryId: Long? = savedStateHandle.get("categoryId")
//    var categoryName by mutableStateOf("")
//        private set

    //    var exams by mutableStateOf<List<ExamWithDetails>>(emptyList())
//        private set
    private val _uiState = MutableStateFlow(ExamListUiState())
    val uiState = _uiState.asStateFlow()

    init {
        if (categoryId != null) {
            loadExams(categoryId)
        }
    }

    fun loadExams(categoryId: Long) {

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {

                val name = examRepository.getCategoryName(categoryId)
                val exams = examRepository.getExamsByCategory(categoryId)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        categoryName = name,
                        exams=exams
                    )
                }
            }catch (e:Exception){
                _uiState.update {
                    it.copy(isLoading = false, error = e.message)
                }
            }

        }

    }

}
    data class ExamListUiState(
        val isLoading: Boolean = false,
        val categoryName: String = "",
        val exams: List<ExamWithDetails> = emptyList(),
        val error: String? = null
    )
