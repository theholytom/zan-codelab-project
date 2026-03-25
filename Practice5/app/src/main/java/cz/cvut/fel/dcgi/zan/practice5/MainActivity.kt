package cz.cvut.fel.dcgi.zan.practice5

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import cz.cvut.fel.dcgi.zan.practice5.ui.PlansRoute
import cz.cvut.fel.dcgi.zan.practice5.ui.PlansScreen
import cz.cvut.fel.dcgi.zan.practice5.ui.PlaygroundDetailRoute
import cz.cvut.fel.dcgi.zan.practice5.ui.PlaygroundDetailScreen
import cz.cvut.fel.dcgi.zan.practice5.ui.PlaygroundListRoute
import cz.cvut.fel.dcgi.zan.practice5.ui.PlaygroundListScreen
import cz.cvut.fel.dcgi.zan.practice5.ui.theme.Practice5Theme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Practice5Theme {
                PlaygroundApp()
            }
        }
    }
}

@Composable
fun PlaygroundApp() {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    val bottomNavItems = listOf(
        Triple(PlaygroundListRoute, "Playgrounds", Icons.Default.Place),
        Triple(PlansRoute, "Plans", Icons.Default.DateRange),
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            val currentEntry by navController.currentBackStackEntryAsState()
            val currentRoute = currentEntry?.destination?.route
            // Show bottom bar only on top-level destinations
            val showBottomBar = bottomNavItems.any {
                currentRoute?.contains(it.first::class.simpleName ?: "") == true
            }
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { (route, label, icon) ->
                        NavigationBarItem(
                            selected = currentRoute?.contains(route::class.simpleName ?: "") == true,
                            onClick = {
                                navController.navigate(route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(icon, contentDescription = label) },
                            label = { Text(label) },
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = PlaygroundListRoute,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable<PlaygroundListRoute> {
                PlaygroundListScreen(
                    onNavigateToDetail = { id ->
                        navController.navigate(PlaygroundDetailRoute(id))
                    },

                )
            }
            composable<PlansRoute> {
                PlansScreen(
                    snackbarHostState = snackbarHostState,
                )
            }
            composable<PlaygroundDetailRoute> {
                PlaygroundDetailScreen(
                    onNavigateUp = { navController.navigateUp() },
                )
            }
        }
    }
}
