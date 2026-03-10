package cz.cvut.fel.zan.practice4nav.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun OnboardingNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "Welcome",   // routes are plain strings
    ) {
        composable("Welcome") {
            WelcomeScreen(
                onContinue = {
                    navController.navigate("Login")
                }
            )
        }

        composable("Login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("Home")
                },
                onBack = {
                    navController.navigateUp()
                }
            )
        }

        composable("Home") {
            HomeScreen()
        }
    }
}
