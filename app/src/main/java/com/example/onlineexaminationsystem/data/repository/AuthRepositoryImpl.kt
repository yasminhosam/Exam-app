package com.example.onlineexaminationsystem.data.repository

import com.example.onlineexaminationsystem.data.mapper.toDto
import com.example.onlineexaminationsystem.data.remote.UserDto
import com.example.onlineexaminationsystem.domain.model.BasicSession
import com.example.onlineexaminationsystem.domain.model.Role
import com.example.onlineexaminationsystem.domain.model.User
import com.example.onlineexaminationsystem.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override suspend fun signUp(name: String, email: String, password: String, role: Role): Result<User> {
        return try {

            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val currentUser = authResult.user ?: throw Exception("SignUp failed")

            //  Set username
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(name).build()
            currentUser.updateProfile(profileUpdates).await()

            //  Send verification email
            currentUser.sendEmailVerification().await()


            val user = User(
                id = currentUser.uid,
                name = name,
                email = email,
                role = role
            )


            firestore.collection("users").document(currentUser.uid).set(user.toDto()).await()


            Result.success(user)

        } catch (e: Exception) {

            Result.failure(e)
        }
    }

    override suspend fun login(email: String, password: String): Result<User> {
        return try {

            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val currentUser = authResult.user ?: throw Exception("Login failed")


            val document = firestore.collection("users").document(currentUser.uid).get().await()
            val dto = document.toObject(UserDto::class.java) ?: throw Exception("User data missing from database")


            val user = User(
                id = dto.id,
                name = dto.name,
                email = dto.email,
                role = Role.valueOf(dto.role)
            )
            Result.success(user)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }



    override suspend fun logout() {
        firebaseAuth.signOut()
    }

    override fun getCurrentSession(): BasicSession? {
        val firebaseUser=firebaseAuth.currentUser?: return null
        return BasicSession(
            id = firebaseUser.uid,
            name = firebaseUser.displayName ?: "User",
            email = firebaseUser.email ?: ""
        )
    }

    override suspend fun isEmailVerified(): Boolean {
        val user = firebaseAuth.currentUser
        user?.reload()?.await()
        return user?.isEmailVerified ?: false
    }
}