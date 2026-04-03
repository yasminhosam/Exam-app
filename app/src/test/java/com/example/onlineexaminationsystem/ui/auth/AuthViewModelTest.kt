package com.example.onlineexaminationsystem.ui.auth

import com.example.onlineexaminationsystem.FakeAuthRepository
import com.example.onlineexaminationsystem.domain.model.Role
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {
    private lateinit var fakeRepository: FakeAuthRepository
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup(){
        Dispatchers.setMain(StandardTestDispatcher())
        fakeRepository= FakeAuthRepository()
        viewModel= AuthViewModel(fakeRepository)

    }
    @After
    fun tearDown(){
        Dispatchers.resetMain()
    }

    @Test
    fun `signup with empty email updates emailError state`(){
        viewModel.onEmailChange("")
        viewModel.onSignUpClick()
        val currentSate=viewModel.uiState.value
        assertEquals("Email cannot be empty",currentSate.emailError)
    }
    @Test
    fun `signup with invalid email format updates emailError state`(){
        viewModel.onEmailChange("invalid-email")
        viewModel.onSignUpClick()
        val currentSate=viewModel.uiState.value
        assertEquals("Invalid email format",currentSate.emailError)
    }

    @Test
    fun `signup with short username updates usernameError state`(){
        viewModel.onUsernameChange("w")
        viewModel.onSignUpClick()
        val currentSate=viewModel.uiState.value
        assertEquals("Your name must be at least two characters",currentSate.usernameError)
    }

    @Test
    fun `signup with password missing number updates passwordError state`(){
        viewModel.onPasswordChange("ValidLengthNoNumber")
        viewModel.onSignUpClick()
        val currentSate=viewModel.uiState.value
        assertEquals("Password must contain a number",currentSate.passwordError)
    }
    @Test
    fun `signup with password missing uppercase letter updates passwordError state`(){
        viewModel.onPasswordChange("validlengthnouppercase123")
        viewModel.onSignUpClick()
        val currentSate=viewModel.uiState.value
        assertEquals("Password must contain an uppercase letter",currentSate.passwordError)
    }

    @Test
    fun  `signup with password less than 6 characters updates passwordError state`(){
        viewModel.onPasswordChange("len")
        viewModel.onSignUpClick()
        val currentSate=viewModel.uiState.value
        assertEquals("Password must be at least 6 characters",currentSate.passwordError)
    }

    @Test
    fun `signup with valid credentials navigates to student`()= runTest{
        viewModel.onUsernameChange("ValidName")
        viewModel.onEmailChange("valid@gmail.com")
        viewModel.onPasswordChange("ValidPass123")
        viewModel.onRoleChange(Role.STUDENT)

        val event=backgroundScope.async {
            viewModel.events.first()
        }
        viewModel.onSignUpClick()

        advanceUntilIdle()

        assertEquals(AuthEvent.NavigateToStudent,event.await())

    }
    @Test
    fun `signup with valid credentials navigates to teacher`()= runTest{
        viewModel.onUsernameChange("ValidName")
        viewModel.onEmailChange("valid@gmail.com")
        viewModel.onPasswordChange("ValidPass123")
        viewModel.onRoleChange(Role.TEACHER)

        val event=backgroundScope.async {
            viewModel.events.first()
        }
        viewModel.onSignUpClick()

        advanceUntilIdle()

        assertEquals(AuthEvent.NavigateToTeacher,event.await())

    }

    @Test
    fun `login with wrong credentials updates globalError state`()= runTest{
        viewModel.onEmailChange("fake@gmail.com")
        viewModel.onPasswordChange("ValidPass123")

        viewModel.onLoginClick()
        advanceUntilIdle()

        val currentSate=viewModel.uiState.value
        assertEquals("Invalid email or password",currentSate.globalError)

    }
    @Test
    fun `signup with existing email updates globalError state`() = runTest {
        // 1. Arrange
        viewModel.onUsernameChange("ValidName")
        viewModel.onEmailChange("used@gmail.com")
        viewModel.onPasswordChange("ValidPass123")
        viewModel.onRoleChange(Role.STUDENT)

        viewModel.onSignUpClick()
        advanceUntilIdle()

        // 2. Act:
        viewModel.onSignUpClick()
        advanceUntilIdle()

        // 3. Assert:
        val currentState = viewModel.uiState.value
        assertEquals("Email already in use", currentState.globalError)
    }

    // BONUS: Testing a successful login!
    @Test
    fun `login with valid credentials navigates to student`() = runTest {

        fakeRepository.signUp("Real Student", "real@gmail.com", "ValidPass123", Role.STUDENT)

        //Arrange:
        viewModel.onEmailChange("real@gmail.com")
        viewModel.onPasswordChange("ValidPass123")


        val event = backgroundScope.async {
            viewModel.events.first()
        }

        //Act:
        viewModel.onLoginClick()
        advanceUntilIdle()

        //  Assert:
        assertEquals(AuthEvent.NavigateToStudent, event.await())
    }


}