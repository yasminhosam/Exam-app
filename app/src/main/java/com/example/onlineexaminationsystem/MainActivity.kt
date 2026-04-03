package com.example.onlineexaminationsystem

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.onlineexaminationsystem.domain.repository.AuthRepository
import com.example.onlineexaminationsystem.ui.navigation.NavGraph
import com.example.onlineexaminationsystem.ui.navigation.Screen

import com.example.onlineexaminationsystem.ui.theme.OnlineExaminationSystemTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var authRepository: AuthRepository
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
           OnlineExaminationSystemTheme {
               val navController= rememberNavController()
               val session =authRepository.getCurrentSession()
               val startRoute=if(session==null) Screen.Login.route
               else Screen.Home.route
               Log.d("MainActivity", "onCreate: $startRoute")
               Scaffold(
                   modifier = Modifier.fillMaxSize()
               ) {innerPadding ->

               NavGraph(
                   navController, Modifier.padding(innerPadding),
                   startDestination = startRoute
               )
               }
           }


        }
    }
}
