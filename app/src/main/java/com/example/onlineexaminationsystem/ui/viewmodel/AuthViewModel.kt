package com.example.onlineexaminationsystem.ui.viewmodel

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlineexaminationsystem.data.model.Role
import com.example.onlineexaminationsystem.data.repository.AuthRepository
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
    private val authRepository: AuthRepository
) : ViewModel() {
    val currentUser = authRepository.currentUser

    var loginEmail by mutableStateOf("")
    var loginPassword by mutableStateOf("")

    var signUpUsername by mutableStateOf("")
    var signUpEmail by mutableStateOf("")
    var signUpPassword by mutableStateOf("")


    private val _events = MutableSharedFlow<AuthEvent>()
    val events = _events.asSharedFlow()

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()


    fun onLoginClick() {
        val emailError = validateEmail(loginEmail)
        val passError = validatePassword(loginPassword)

        _uiState.update {
            it.copy(
                emailError = emailError,
                passwordError = passError,
                globalError = null
            )
        }

        if ( emailError != null || passError != null) {
            return
        }

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading=true) }

                val user = authRepository.login(loginEmail, loginPassword)
                _uiState.update { it.copy(isLoading = false) }
                if (user != null) {
                   if(user.role==Role.STUDENT)
                      _events.emit(AuthEvent.NavigateToStudent)
                    else
                       _events.emit(AuthEvent.NavigateToAdmin)
                }else {
                    _uiState.update { it.copy(globalError = "Invalid Email ot Password")}
                }
            }catch (e:Exception){
                _uiState.update { it.copy(globalError ="Login failed ${e.message}" ) }

            }


        }
    }


    fun onSignUpClick() {
        val nameError = validateName()
        val emailError = validateEmail(signUpEmail)
        val passError = validatePassword(signUpPassword)

        _uiState.update {
            it.copy(
                usernameError = nameError,
                emailError = emailError,
                passwordError = passError,
                globalError = null
            )
        }

        if (nameError != null || emailError != null || passError != null) {
            return
        }
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading=true) }
                authRepository.registerStudent(signUpUsername, signUpEmail, signUpPassword)
                _uiState.update { it.copy(isLoading = false) }
                _events.emit(AuthEvent.NavigateToStudent)

            }catch (e:Exception){
                _uiState.update { it.copy(globalError ="SignUp failed ${e.message}" ) }
            }
        }

    }


    fun logout() {
        authRepository.logout()
    }

    private fun validateName():String?{
        if (signUpUsername.trim().length <2) return "Your name must be at least two characters"
       return null
    }
    private fun validateEmail(email:String):String?{
        if(email.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()) return null
        return "Invalid email format"
    }
    private fun validatePassword(password:String):String?{
        if(password.length < 6) return "Password must be at least 6 characters"
        if(!password.any{it.isDigit()}) return "Password must contain a number"
        if(!password.any { it.isUpperCase() }) return "Password must contain an uppercase letter"
        return null
    }


}
    data class AuthUiState(
        val isLoading: Boolean = false,
        val globalError: String? = null,
        val emailError: String? = null,
        val passwordError: String? = null,
        val usernameError: String? = null
    )
    sealed class AuthEvent {
        object NavigateToStudent : AuthEvent()
        object NavigateToAdmin : AuthEvent()
    }
