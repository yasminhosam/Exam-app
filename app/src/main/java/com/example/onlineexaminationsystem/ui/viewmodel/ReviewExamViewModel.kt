package com.example.onlineexaminationsystem.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlineexaminationsystem.data.model.AnswerSnapshot
import com.example.onlineexaminationsystem.data.repository.StudentRepository
import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ReviewExamViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val studentRepository: StudentRepository
) : ViewModel() {

    private val submittedExamId: Long? = savedStateHandle.get<Long>("examId")
//    var snapshots by mutableStateOf<List<AnswerSnapshot>>(emptyList())
//   var currentIndex by mutableStateOf(0)
//     private set
//    val currentSnapshot: AnswerSnapshot?
//        get() = snapshots.getOrNull(currentIndex)

    private val _uiState = MutableStateFlow(ReviewExamUiState())
    val uiState = _uiState.asStateFlow()

    init {
        if (submittedExamId != null) {
            loadExam(submittedExamId)
        }
    }

    fun loadExam(submittedExamId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val fetchedSnapshots = studentRepository.getAnswerSnapshots(submittedExamId)

                // Update State: Save list, reset index to 0, stop loading
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        snapshots = fetchedSnapshots,
                        currentIndex = 0
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }

    }

    fun onNextClick() {
        if (_uiState.value.currentIndex < _uiState.value.snapshots.size -1)
            _uiState.update { it.copy(currentIndex = it.currentIndex + 1) }

    }

    fun onPreviousClick() {
        if (_uiState.value.currentIndex > 0)
            _uiState.update { it.copy(currentIndex = it.currentIndex - 1) }
    }

}

data class ReviewExamUiState(
    val isLoading: Boolean = false,
    val currentIndex: Int = 0,
    val snapshots:List<AnswerSnapshot> = emptyList(),
    val error: String? = null
){
    val currentSnapshot: AnswerSnapshot?
        get() = snapshots.getOrNull(currentIndex)
}

