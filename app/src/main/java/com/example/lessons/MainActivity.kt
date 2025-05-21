package com.example.lessons

import SplashScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lessons.Screens.Login.LoginScreen
import com.example.lessons.Screens.Profile.Raspisanie
import com.example.lessons.Screens.Register.RegisterScreen
import com.example.lessons.ui.theme.LessonsTheme
import com.example.lessons.ui.theme.WorkForPerson
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest

val supabase = createSupabaseClient(
    supabaseUrl = "https://rcdpwtohyuhiccjvzooj.supabase.co",
    supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InJjZHB3dG9oeXVoaWNjanZ6b29qIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDc0NTc1MDYsImV4cCI6MjA2MzAzMzUwNn0.OHDOQyrmf8wZLMWzt-MJ8JPAqUBIejzNk4mlESjXbYM"
){
    install(Postgrest)
    install(Auth)
    install(io.github.jan.supabase.storage.Storage)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            LessonsTheme {
                AppNavigator()
            }
        }
    }
}

@Composable
fun AppNavigator() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("content"){ Raspisanie(navController, WorkForPerson()) }
        composable("splash") { SplashScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("register") { RegisterScreen(navController) }
    }
}
