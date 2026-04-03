package com.example.onlineexaminationsystem.ui.exam

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlineexaminationsystem.domain.model.Category
import com.example.onlineexaminationsystem.domain.model.Question
import com.example.onlineexaminationsystem.domain.repository.AuthRepository
import com.example.onlineexaminationsystem.domain.repository.ExamRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import kotlin.time.Duration.Companion.minutes

@HiltViewModel
class CreateExamViewModel @Inject constructor(
    private val examRepository: ExamRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateExamUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<CreateExamEvent>()
    val events = _events.asSharedFlow()
    private val teacherId= authRepository.getCurrentSession()?.id

    init {

        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            val cats = examRepository.getAllCategories().first()
            _uiState.update { it.copy(categories = cats, selectedCategory = cats.firstOrNull()) }
        }
    }

    fun onTitleChange(value: String) = _uiState.update { it.copy(title = value, titleError = null) }
    fun onDurationChange(value: String) = _uiState.update { it.copy(durationInput = value, durationError = null) }
    fun onPassPercentageChange(value: String) = _uiState.update { it.copy(passPercentageInput = value, passPercentageError = null) }
    fun onCategorySelected(category: Category) = _uiState.update { it.copy(selectedCategory = category) }

    fun addQuestion() = _uiState.update { it.copy(questions = it.questions + QuestionDraft(id = UUID.randomUUID().toString())) }
    fun removeQuestion(id: String) = _uiState.update { it.copy(questions = it.questions.filter { q -> q.id != id }) }

    fun onQuestionTextChange(id: String, text: String) = _uiState.update {
        it.copy(questions = it.questions.map { q -> if (q.id == id) q.copy(text = text, textError = null) else q })
    }

    fun onOptionChange(questionId: String, optionIndex: Int, value: String) = _uiState.update {
        it.copy(questions = it.questions.map { q ->
            if (q.id == questionId) {
                val opts = q.options.toMutableList().also { list -> list[optionIndex] = value }
                q.copy(options = opts, optionsError = null)
            } else q
        })
    }

    fun onCorrectOptionChange(questionId: String, index: Int) = _uiState.update {
        it.copy(questions = it.questions.map { q -> if (q.id == questionId) q.copy(correctOptionIndex = index) else q })
    }

    fun onMarkChange(questionId: String, value: String) = _uiState.update {
        it.copy(questions = it.questions.map { q -> if (q.id == questionId) q.copy(markInput = value) else q })
    }

    fun onSaveExam() {
        val currentTeacherId = teacherId


        if (currentTeacherId == null) {
            _uiState.update { it.copy(globalError = "Session expired. Please log in again.") }
            return
        }
        val state = _uiState.value

        val titleError = if (state.title.isBlank()) "Title is required" else null
        val duration = state.durationInput.toIntOrNull()
        val durationError = if (duration == null || duration < 1) "Enter a valid duration in minutes" else null
        val passPercentage = state.passPercentageInput.toIntOrNull()
        val passError = if (passPercentage == null || passPercentage !in 1..100) "Enter a value between 1 and 100" else null
        val categoryError = if (state.selectedCategory == null) "Select a category" else null

        val validatedQuestions = state.questions.map { q ->
            q.copy(
                textError = if (q.text.isBlank()) "Question text is required" else null,
                optionsError = if (q.options.any { it.isBlank() }) "Fill all options" else null
            )
        }
        val hasQuestionErrors = validatedQuestions.any { it.textError != null || it.optionsError != null }

        _uiState.update {
            it.copy(
                titleError = titleError, durationError = durationError,
                passPercentageError = passError, categoryError = categoryError,
                questions = validatedQuestions,
                globalError = if (state.questions.isEmpty()) "Add at least one question" else null
            )
        }

        if (titleError != null || durationError != null || passError != null ||
            categoryError != null || hasQuestionErrors || state.questions.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, globalError = null) }
            try {
                val questions = state.questions.map { draft ->
                    Question(
                        id = draft.id,
                        examId = "",       // addExam re-assigns examId internally
                        text = draft.text.trim(),
                        options = draft.options.map { it.trim() },
                        correctAnswer = draft.correctOptionIndex,
                        mark = draft.markInput.toIntOrNull()?.coerceAtLeast(1) ?: 1,
                        isSynced = false
                    )
                }.toMutableList()

                // teacherId stored in Room teacher_id column and Firestore document
                examRepository.addExam(
                    teacherId = currentTeacherId,
                    title = state.title.trim(),
                    category = state.selectedCategory!!,
                    questions = questions,
                    duration = duration!!.minutes,
                    passPercentage = passPercentage!!
                )
                _events.emit(CreateExamEvent.ExamCreated)
            } catch (e: Exception) {
                _uiState.update { it.copy(globalError = e.message ?: "Failed to save exam") }
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}

data class QuestionDraft(
    val id: String = "",
    val text: String = "",
    val options: List<String> = List(4) { "" },
    val correctOptionIndex: Int = 0,
    val markInput: String = "1",
    val textError: String? = null,
    val optionsError: String? = null
)

data class CreateExamUiState(
    val title: String = "",
    val durationInput: String = "30",
    val passPercentageInput: String = "50",
    val categories: List<Category> = emptyList(),
    val selectedCategory: Category? = null,
    val questions: List<QuestionDraft> = emptyList(),
    val isLoading: Boolean = false,
    val globalError: String? = null,
    val titleError: String? = null,
    val durationError: String? = null,
    val passPercentageError: String? = null,
    val categoryError: String? = null
)

sealed class CreateExamEvent {
    object ExamCreated : CreateExamEvent()
}