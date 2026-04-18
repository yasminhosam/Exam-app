package com.example.onlineexaminationsystem.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlineexaminationsystem.domain.model.Role
import com.example.onlineexaminationsystem.domain.repository.AuthRepository
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

sealed class SplashEvent {
    object Loading : SplashEvent()
    object NavigateToLogin : SplashEvent()
    object NavigateToStudent : SplashEvent()
    object NavigateToTeacher : SplashEvent()
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _navigationEvent = MutableStateFlow<SplashEvent>(SplashEvent.Loading)
    val navigationEvent = _navigationEvent.asStateFlow()

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        viewModelScope.launch {
            try {
                val session = authRepository.getCurrentSession()
                if (session != null) {
                    val document = firestore.collection("users").document(session.id).get().await()
                    val userRoleString = document.getString("role")

                    if (userRoleString == Role.STUDENT.name) {
                        _navigationEvent.value = SplashEvent.NavigateToStudent
                    } else {
                        _navigationEvent.value = SplashEvent.NavigateToTeacher
                    }
                } else {
                    _navigationEvent.value = SplashEvent.NavigateToLogin
                }
            } catch (e: Exception) {

                _navigationEvent.value = SplashEvent.NavigateToLogin
            }
        }
    }
}