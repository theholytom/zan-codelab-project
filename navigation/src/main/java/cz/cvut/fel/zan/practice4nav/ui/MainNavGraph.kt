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
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true    // ← save the popped back stack
                        }
                        launchSingleTop = true
                        restoreState = true     // ← restore the saved stack when returning
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
