package com.example.onlineexaminationsystem.ui.topstudent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlineexaminationsystem.domain.model.TopStudent
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class TopStudentsViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _uiState = MutableStateFlow(TopStudentsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadTopStudents()
    }

    fun loadTopStudents() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {

                val snapshot = firestore.collection("submitted_exams")
                    .orderBy("score", com.google.firebase.firestore.Query.Direction.DESCENDING)
                    .limit(20)
                    .get()
                    .await()


                val topStudents = snapshot.documents
                    .mapNotNull { doc ->
                        val name = doc.getString("studentName") ?: return@mapNotNull null
                        val score = doc.getLong("score")?.toInt() ?: return@mapNotNull null
                        val gradeStr = doc.getString("grade") ?: "F"
                        val grade = gradeStr.firstOrNull() ?: 'F'
                        TopStudent(name = name, score = score, grade = grade)
                    }

                    .groupBy { it.name }
                    .map { (_, submissions) -> submissions.maxByOrNull { it.score }!! }
                    .sortedByDescending { it.score }
                    .take(10)

                _uiState.update { it.copy(topStudents = topStudents, isLoading = false) }

            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message ?: "Failed to load leaderboard", isLoading = false) }
            }
        }
    }
}

data class TopStudentsUiState(
    val topStudents: List<TopStudent> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)