package com.example.onlineexaminationsystem.ui.exam

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlineexaminationsystem.domain.model.AnswerSnapshot
import com.example.onlineexaminationsystem.domain.repository.StudentRepository
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

    private val submittedExamId = savedStateHandle.get<String>("examId")

    private val _uiState = MutableStateFlow(ReviewExamUiState())
    val uiState = _uiState.asStateFlow()

    init {

        submittedExamId?.let { loadExam(it) }

    }

    fun loadExam(submittedExamId: String) {

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                studentRepository
                    .getAnswerSnapshots(submittedExamId)
                    .collect { snapshots ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                snapshots = snapshots
                            )
                        }
                    }
            }catch (e:Exception){
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

