package cz.cvut.fel.zan.practice4nav.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

@Composable
fun MainNavGraph() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination

    Scaffold(
        bottomBar = {
            MainBottomBar(
                currentDestination = currentDestination,
                onNavigate = { route ->
                    navController.navigate(route) {
                        // Pop everything up to (and including) the start destination,
                        // then re-launch it. This means pressing back from any tab
                        // first returns to Home, and a second back exits the app.
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = false   // keep Home in the stack
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = MainHome,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable<MainHome>     { HomeScreen() }
            composable<MainPlayground>     { PlaygroundScreen() }
            composable<MainSettings> { SettingsScreen() }
        }
    }
}
