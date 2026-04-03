package com.example.onlineexaminationsystem
import com.example.onlineexaminationsystem.domain.model.BasicSession
import com.example.onlineexaminationsystem.domain.model.Role
import com.example.onlineexaminationsystem.domain.model.User
import com.example.onlineexaminationsystem.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser

class FakeAuthRepository:AuthRepository {
    private val users=mutableListOf<User>()
    private val passwords= mutableMapOf<String,String>()
    private var currentUser:User?=null
    var shouldEmailBeVerified = true
    override suspend fun signUp(
        name: String,
        email: String,
        password: String,
        role: Role
    ): Result<User> {
        if( users.any{it.email== email}){
            return Result.failure(Exception("Email already in use"))
        }
        val user=User("fake_id_${users.size}",name,email,role)
        users.add(user)
        passwords[email]=password
        currentUser=user
        return Result.success(user)
    }

    override suspend fun login(email: String, password: String): Result<User> {
        val user=users.firstOrNull{it.email ==email}
        return if(user!=null && passwords[email]==password){
            currentUser=user
            Result.success(user)
        }else{
            Result.failure(Exception("Invalid email or password"))
        }
    }

    override suspend fun logout() {
        currentUser=null

    }

    override fun getCurrentSession(): BasicSession? {

        val user = currentUser ?: return null

        return BasicSession(
            id = user.id,
            name = user.name,
            email = user.email
        )
    }


    override suspend fun isEmailVerified(): Boolean {
        return shouldEmailBeVerified
    }
}