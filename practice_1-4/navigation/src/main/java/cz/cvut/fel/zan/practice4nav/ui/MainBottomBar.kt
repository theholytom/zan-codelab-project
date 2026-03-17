package cz.cvut.fel.zan.practice4nav.ui

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavDestination

@Composable
fun MainBottomBar(
    currentDestination: NavDestination?,
    onNavigate: (Any) -> Unit,
) {
    NavigationBar {
        mainNavBarItems.forEach { item ->
            NavigationBarItem(
                selected = currentDestination.isRoute(item.route),
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                    )
                },
                label = { Text(item.label) },
            )
        }
    }
}
