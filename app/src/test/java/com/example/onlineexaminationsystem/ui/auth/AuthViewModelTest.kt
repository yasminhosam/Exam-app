package com.example.onlineexaminationsystem.ui.auth

import com.example.onlineexaminationsystem.FakeAuthRepository
import com.example.onlineexaminationsystem.domain.model.AnswerSnapshot
import com.example.onlineexaminationsystem.domain.model.Category
import com.example.onlineexaminationsystem.domain.model.Exam
import com.example.onlineexaminationsystem.domain.model.ExamWithDetails
import com.example.onlineexaminationsystem.domain.model.Question
import com.example.onlineexaminationsystem.domain.model.Role
import com.example.onlineexaminationsystem.domain.model.SubmittedExam
import com.example.onlineexaminationsystem.domain.repository.ExamRepository
import com.example.onlineexaminationsystem.domain.repository.StudentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
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
import kotlin.time.Duration

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {
    private lateinit var fakeRepository: FakeAuthRepository
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        fakeRepository = FakeAuthRepository()
        val dummyExamRepository = object : ExamRepository { /* implement empty members */
            override fun getExamsByCategory(categoryId: String): Flow<List<ExamWithDetails>> {
               return emptyFlow()
            }

            override suspend fun getExamById(id: String): ExamWithDetails {
                return  return ExamWithDetails(
                    exam = Exam(
                        id = id,
                        teacherId = "",
                        title = "",
                        categoryId = "",
                        duration = Duration.ZERO,
                        passPercentage = 0,
                        totalScore = 0,
                        isSynced = false,
                        isDeleted = false
                    ),
                    questions = emptyList(),
                    category = Category(
                        id = "",
                        name = "",
                        imageRes = ""
                    )
                )
            }

            override fun getAllCategories(): Flow<List<Category>> {
                return emptyFlow()
            }

            override fun getAllExams(): Flow<List<ExamWithDetails>> {
               return emptyFlow()
            }

            override suspend fun getCategoryName(categoryId: String): String {
                return ""
            }

            override fun getExamsByTeacher(teacherId: String): Flow<List<ExamWithDetails>> {
                return emptyFlow()
            }

            override suspend fun addExam(
                teacherId: String,
                title: String,
                category: Category,
                questions: MutableList<Question>,
                duration: Duration,
                passPercentage: Int
            ) {

            }

            override suspend fun addQuestionToExam(
                examId: String,
                text: String,
                options: List<String>,
                correctAnswerIndex: Int,
                mark: Int
            ) {

            }

            override suspend fun deleteExam(id: String) {

            }

            override suspend fun fetchTeacherExamsFromCloud(teacherId: String) {

            }

            override suspend fun fetchAllAvailableExamsFromCloud() {

            }

            override suspend fun updateExamWithQuestions(
                examId: String,
                title: String,
                categoryId: String,
                questions: MutableList<Question>,
                duration: Duration,
                passPercentage: Int
            ) {

            }
        }
        val dummyStudentRepository = object : StudentRepository { /* implement empty members */
            override suspend fun submitExam(
                studentId: String,
                studentName: String,
                examWithDetails: ExamWithDetails,
                studentAnswers: Map<String, Int>
            ) {
            }

            override fun getExamHistory(studentId: String): Flow<List<SubmittedExam>> {
                return emptyFlow()
            }

            override fun getAnswerSnapshots(submittedExamId: String): Flow<List<AnswerSnapshot>> {
                return emptyFlow()
            }

            override suspend fun fetchStudentHistoryFromCloud(studentId: String) {

            }
        }

        viewModel = AuthViewModel(
            authRepository = fakeRepository,
            examRepository = dummyExamRepository,
            studentRepository = dummyStudentRepository
        )

    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `signup with empty email updates emailError state`() {
        viewModel.onEmailChange("")
        viewModel.onSignUpClick()
        val currentSate = viewModel.uiState.value
        assertEquals("Email cannot be empty", currentSate.emailError)
    }

    @Test
    fun `signup with invalid email format updates emailError state`() {
        viewModel.onEmailChange("invalid-email")
        viewModel.onSignUpClick()
        val currentSate = viewModel.uiState.value
        assertEquals("Invalid email format", currentSate.emailError)
    }

    @Test
    fun `signup with short username updates usernameError state`() {
        viewModel.onUsernameChange("w")
        viewModel.onSignUpClick()
        val currentSate = viewModel.uiState.value
        assertEquals("Your name must be at least two characters", currentSate.usernameError)
    }

    @Test
    fun `signup with password missing number updates passwordError state`() {
        viewModel.onPasswordChange("ValidLengthNoNumber")
        viewModel.onSignUpClick()
        val currentSate = viewModel.uiState.value
        assertEquals("Password must contain a number", currentSate.passwordError)
    }

    @Test
    fun `signup with password missing uppercase letter updates passwordError state`() {
        viewModel.onPasswordChange("validlengthnouppercase123")
        viewModel.onSignUpClick()
        val currentSate = viewModel.uiState.value
        assertEquals("Password must contain an uppercase letter", currentSate.passwordError)
    }

    @Test
    fun `signup with password less than 6 characters updates passwordError state`() {
        viewModel.onPasswordChange("len")
        viewModel.onSignUpClick()
        val currentSate = viewModel.uiState.value
        assertEquals("Password must be at least 6 characters", currentSate.passwordError)
    }

    @Test
    fun `signup with valid credentials navigates to student`() = runTest {
        viewModel.onUsernameChange("ValidName")
        viewModel.onEmailChange("valid@gmail.com")
        viewModel.onPasswordChange("ValidPass123")
        viewModel.onRoleChange(Role.STUDENT)

        val event = backgroundScope.async {
            viewModel.events.first()
        }
        viewModel.onSignUpClick()

        advanceUntilIdle()

        assertEquals(AuthEvent.NavigateToStudent, event.await())

    }

    @Test
    fun `signup with valid credentials navigates to teacher`() = runTest {
        viewModel.onUsernameChange("ValidName")
        viewModel.onEmailChange("valid@gmail.com")
        viewModel.onPasswordChange("ValidPass123")
        viewModel.onRoleChange(Role.TEACHER)

        val event = backgroundScope.async {
            viewModel.events.first()
        }
        viewModel.onSignUpClick()

        advanceUntilIdle()

        assertEquals(AuthEvent.NavigateToTeacher, event.await())

    }

    @Test
    fun `login with wrong credentials updates globalError state`() = runTest {
        viewModel.onEmailChange("fake@gmail.com")
        viewModel.onPasswordChange("ValidPass123")

        viewModel.onLoginClick()
        advanceUntilIdle()

        val currentSate = viewModel.uiState.value
        assertEquals("Invalid email or password", currentSate.globalError)

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

    @Test
    fun `forget password with empty email updates emailError state`() {
        viewModel.onEmailChange("")
        viewModel.onForgetPasswordClick()
        val currentState = viewModel.uiState.value
        assertEquals("Email cannot be empty", currentState.emailError)

    }

    @Test
    fun `forget password with invalid email format updates emailError ` (){
        viewModel.onEmailChange("invalid-email")
        viewModel.onForgetPasswordClick()
        val currentState = viewModel.uiState.value
        assertEquals("Invalid email format", currentState.emailError)
    }
    @Test
    fun `forget password with registered email emits SendResetPasswordLink event`()= runTest{
        fakeRepository.signUp("Real Student", "real@gmail.com", "ValidPass123", Role.STUDENT)
        viewModel.onEmailChange("real@gmail.com")

        val event=backgroundScope.async {
            viewModel.events.first()
        }

        //Act
        viewModel.onForgetPasswordClick()
        advanceUntilIdle()

        //Assert
        assertEquals(AuthEvent.SendResetPasswordLink,event.await())

    }
    @Test
    fun `forget password with unregistered email emits ShowSnackbar event`()= runTest{
        viewModel.onEmailChange("nobody@gmail.com")

        val event=backgroundScope.async {
            viewModel.events.first()
        }

        //Act
        viewModel.onForgetPasswordClick()
        advanceUntilIdle()

        //Assert
        val expectedEvent = AuthEvent.ShowError("This email is not registered. Please Sign Up first.")
        assertEquals(expectedEvent, event.await())
    }

}
