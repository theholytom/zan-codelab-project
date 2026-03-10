package cz.cvut.fel.zan.practice4nav.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable


@Serializable
data object Welcome

@Serializable
data object Login

@Serializable
data object Home
@Composable
fun OnboardingNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Welcome,         // pass the object, not a string
    ) {
        composable<Welcome> {
            WelcomeScreen(
                onContinue = {
                    navController.navigate(Login) // navigate to the object
                }
            )
        }

        composable<Login> {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Home) {
                        popUpTo<Welcome> { inclusive = true }
                    }
                },
                onBack = { navController.navigateUp() }
            )
        }

        composable<Home> {
            HomeScreen()
        }
    }
}
