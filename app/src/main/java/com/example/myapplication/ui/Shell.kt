package com.example.myapplication.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.myapplication.ui.screen.*

private data class BottomItem(val route: String, val label: String, val icon: @Composable () -> Unit)

@Composable
fun Shell(onLogout: () -> Unit) {
    val nav = rememberNavController()

    val items = listOf(
        BottomItem(Routes.FEED, "Feed") { Icon(Icons.Filled.Home, contentDescription = null) },
        BottomItem(Routes.SEARCH, "Search") { Icon(Icons.Filled.Search, contentDescription = null) },
        BottomItem(Routes.FAVORITES, "Favorites") { Icon(Icons.Filled.Favorite, contentDescription = null) }
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Endterm") },
                actions = { TextButton(onClick = onLogout) { Text("Logout") } }
            )
        },
        bottomBar = {
            NavigationBar {
                val backStack by nav.currentBackStackEntryAsState()
                val current = backStack?.destination?.route
                items.forEach { item ->
                    NavigationBarItem(
                        selected = current == item.route,
                        onClick = {
                            nav.navigate(item.route) {
                                popUpTo(nav.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = item.icon,
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = Routes.FEED,
            modifier = Modifier.padding(padding)
        ) {
            composable(Routes.FEED) {
                FeedScreen(onOpenDetails = { lat, lon, name -> nav.navigate("details/$lat/$lon/$name") })
            }
            composable(Routes.SEARCH) {
                SearchScreen2(onOpenDetails = { lat, lon, name -> nav.navigate("details/$lat/$lon/$name") })
            }
            composable(Routes.FAVORITES) {
                FavoritesScreen(onOpenDetails = { lat, lon, name -> nav.navigate("details/$lat/$lon/$name") })
            }

            composable(
                route = "details/{lat}/{lon}/{name}",
                arguments = listOf(
                    navArgument("lat") { type = NavType.StringType },
                    navArgument("lon") { type = NavType.StringType },
                    navArgument("name") { type = NavType.StringType }
                )
            ) { backStack ->
                val lat = backStack.arguments?.getString("lat").orEmpty().toDoubleOrNull() ?: 0.0
                val lon = backStack.arguments?.getString("lon").orEmpty().toDoubleOrNull() ?: 0.0
                val name = backStack.arguments?.getString("name").orEmpty()
                DetailsScreen2(lat = lat, lon = lon, name = name, onOpenComments = { nav.navigate("comments/$name") })
            }

            composable(
                route = "comments/{name}",
                arguments = listOf(navArgument("name") { type = NavType.StringType })
            ) { backStack ->
                val name = backStack.arguments?.getString("name").orEmpty()
                CommentsScreen(name = name)
            }
        }
    }
}
