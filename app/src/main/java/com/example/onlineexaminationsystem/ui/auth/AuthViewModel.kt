package com.example.onlineexaminationsystem.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlineexaminationsystem.domain.model.Role
import com.example.onlineexaminationsystem.domain.repository.AuthRepository
import com.example.onlineexaminationsystem.domain.repository.ExamRepository
import com.example.onlineexaminationsystem.domain.repository.StudentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val examRepository: ExamRepository,
    private val studentRepository: StudentRepository
) : ViewModel() {
    private val _events = MutableSharedFlow<AuthEvent>()
    val events = _events.asSharedFlow()

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()


    fun onLoginClick() {

        val state = _uiState.value

        val emailError = validateEmail(state.email)
        val passwordError = validatePassword(state.password)

        _uiState.update {
            it.copy(
                emailError = emailError,
                passwordError = passwordError,
                globalError = null
            )
        }

        if (emailError != null || passwordError != null) return

        viewModelScope.launch {

            _uiState.update { it.copy(isLoading = true) }

            val result = authRepository.login(state.email, state.password)

            _uiState.update { it.copy(isLoading = false) }

            result.onSuccess { user ->
                val isVerified = authRepository.isEmailVerified()
                Log.d("AuthViewModel", "SUCCESS: User logged in. isEmailVerified: $isVerified")
                if (!isVerified) {
                    _events.emit(AuthEvent.NavigateToVerifyEmail)
                    return@onSuccess
                }
                if (user.role == Role.STUDENT){
                    studentRepository.fetchStudentHistoryFromCloud(user.id)
                    examRepository.fetchAllAvailableExamsFromCloud()
                    _events.emit(AuthEvent.NavigateToStudent)
                }
                else {
                    examRepository.fetchTeacherExamsFromCloud(user.id)
                    _events.emit(AuthEvent.NavigateToTeacher)
                }

                Log.d("AuthViewModel", "onLoginClick: ${user.role}")
            }.onFailure { e ->
                Log.e("AuthViewModel", "LOGIN FAILED CRITICALLY: ${e.message}", e)
                _uiState.update {
                    it.copy(globalError = "Wrong email or password.Please try again")
                }
            }
        }
    }


    fun onSignUpClick() {

        val state = _uiState.value

        val nameError = validateName(state.username)
        val emailError = validateEmail(state.email)
        val passwordError = validatePassword(state.password)

        _uiState.update {
            it.copy(
                usernameError = nameError,
                emailError = emailError,
                passwordError = passwordError,
                globalError = null
            )
        }

        if (nameError != null || emailError != null || passwordError != null) return

        viewModelScope.launch {

            _uiState.update { it.copy(isLoading = true) }

            val result = authRepository.signUp(
                name = state.username,
                email = state.email,
                password = state.password,
                role = state.selectedRole
            )

            _uiState.update { it.copy(isLoading = false) }

            result.onSuccess { user ->
                viewModelScope.launch { authRepository.logout() }
                Log.d("AuthViewModel", "isEmailVerified: ${authRepository.isEmailVerified()}")
                if (!authRepository.isEmailVerified()) {
                    _events.emit(AuthEvent.NavigateToVerifyEmail)
                    return@onSuccess
                }

                if (user.role == Role.STUDENT)
                    _events.emit(AuthEvent.NavigateToStudent)
                else
                    _events.emit(AuthEvent.NavigateToTeacher)
            }.onFailure { e ->
                _uiState.update {
                    it.copy(globalError = "Signup failed.Please try again")
                }
                Log.d("AuthViewModel", "onSignUpClick:${e.message}")
            }
        }
    }


    private fun validateName(name: String): String? {
        if (name.trim().length < 2) return "Your name must be at least two characters"
        return null
    }

    private fun validateEmail(email: String): String? {
        if (email.isBlank()) return "Email cannot be empty"
        val emailRegex="^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
        if (!email.matches(emailRegex.toRegex()))
            return "Invalid email format"
        return null
    }

    private fun validatePassword(password: String): String? {
        if (password.length < 6) return "Password must be at least 6 characters"
        if (!password.any { it.isDigit() }) return "Password must contain a number"
        if (!password.any { it.isUpperCase() }) return "Password must contain an uppercase letter"
        return null
    }

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun onUsernameChange(username: String) {
        _uiState.update { it.copy(username = username) }
    }

    fun onRoleChange(role: Role) {
        _uiState.update { it.copy(selectedRole = role) }
    }
}

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val username: String = "",
    val selectedRole: Role = Role.STUDENT,
    val isLoading: Boolean = false,
    val globalError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val usernameError: String? = null
)

sealed class AuthEvent {
    object NavigateToStudent : AuthEvent()
    object NavigateToTeacher : AuthEvent()
    object NavigateToVerifyEmail : AuthEvent()

}