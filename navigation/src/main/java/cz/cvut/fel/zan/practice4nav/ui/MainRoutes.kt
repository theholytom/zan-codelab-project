package cz.cvut.fel.zan.practice4nav.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import kotlinx.serialization.Serializable

@Serializable
data object MainHome

@Serializable
data object MainPlayground

@Serializable
data object MainSettings

@Serializable
data class PlaygroundDetail(val itemId: Int)

data class NavBarItem(
    val route: Any,
    val label: String,
    val icon: ImageVector,
)

val mainNavBarItems = listOf(
    NavBarItem(MainHome,     "Home",     Icons.Default.Home),
    NavBarItem(MainPlayground,     "Playground",     Icons.AutoMirrored.Default.List),
    NavBarItem(MainSettings, "Settings", Icons.Default.Settings),
)

fun NavDestination?.isRoute(route: Any): Boolean =
    this?.hierarchy?.any { it.hasRoute(route::class) } == true
