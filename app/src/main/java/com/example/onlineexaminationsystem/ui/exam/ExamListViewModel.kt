package com.example.onlineexaminationsystem.ui.exam


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlineexaminationsystem.data.repository.ExamRepositoryImpl
import com.example.onlineexaminationsystem.domain.model.ExamWithDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class ExamListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val examRepositoryImpl: ExamRepositoryImpl
) : ViewModel() {
    private val categoryId: String? = savedStateHandle.get("categoryId")

    private val _categoryIdFlow = MutableStateFlow(categoryId)

    val uiState = _categoryIdFlow.flatMapLatest { id ->
        if (id == null) {
            flowOf(ExamListUiState(error = "Category is missing"))
        } else {
            val name = examRepositoryImpl.getCategoryName(id)
            examRepositoryImpl.getExamsByCategory(id).map { exams ->
                ExamListUiState(categoryName = name, exams = exams, isLoading = false)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ExamListUiState(isLoading = true))
}

//    fun loadExams(categoryId: String) {
//
//        viewModelScope.launch {
//            _uiState.update { it.copy(isLoading = true) }
//            try {
//
//                val name = examRepositoryImpl.getCategoryName(categoryId)
//                val exams = examRepositoryImpl.getExamsByCategory(categoryId)
//                _uiState.update {
//                    it.copy(
//                        isLoading = false,
//                        categoryName = name,
//                        exams=exams
//                    )
//                }
//            }catch (e:Exception){
//                _uiState.update {
//                    it.copy(isLoading = false, error = e.message)
//                }
//            }
//
//        }
//
//    }
//
//}
    data class ExamListUiState(
        val isLoading: Boolean = false,
        val categoryName: String = "",
        val exams: List<ExamWithDetails> = emptyList(),
        val error: String? = null
    )
