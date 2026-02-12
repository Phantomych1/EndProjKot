package com.example.myapplication.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.ui.screen.*

object Routes {
    const val HOME = "home"
    const val SEARCH = "search"
    const val DETAILS = "details"
    const val BOARD = "board"
    const val SETTINGS = "settings"
}

@Composable
fun AppNav() {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = Routes.HOME) {
        composable(Routes.HOME) {
            HomeScreen(
                onSearch = { nav.navigate(Routes.SEARCH) },
                onBoard = { nav.navigate(Routes.BOARD) },
                onSettings = { nav.navigate(Routes.SETTINGS) }
            )
        }

        composable(Routes.SEARCH) {
            SearchScreen(
                onBack = { nav.popBackStack() },
                onOpenDetails = { lat, lon, name ->
                    nav.navigate("${Routes.DETAILS}/$lat/$lon/${name}")
                }
            )
        }

        composable(
            route = "${Routes.DETAILS}/{lat}/{lon}/{name}",
            arguments = listOf(
                navArgument("lat") { type = NavType.StringType },
                navArgument("lon") { type = NavType.StringType },
                navArgument("name") { type = NavType.StringType }
            )
        ) { back ->
            val lat = back.arguments?.getString("lat").orEmpty().toDoubleOrNull() ?: 0.0
            val lon = back.arguments?.getString("lon").orEmpty().toDoubleOrNull() ?: 0.0
            val name = back.arguments?.getString("name").orEmpty()
            DetailsScreen(lat = lat, lon = lon, name = name, onBack = { nav.popBackStack() })
        }

        composable(Routes.BOARD) { BoardScreen(onBack = { nav.popBackStack() }) }
        composable(Routes.SETTINGS) { SettingsScreen(onBack = { nav.popBackStack() }) }
    }
}
