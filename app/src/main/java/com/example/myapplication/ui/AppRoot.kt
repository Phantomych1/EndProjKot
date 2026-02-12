package com.example.myapplication.ui

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.example.myapplication.ui.session.SessionViewModel

@Composable
fun AppRoot() {
    val nav = rememberNavController()
    val sessionVm: SessionViewModel = viewModel()
    val user by sessionVm.user.collectAsState()

    val start = if (user == null) Routes.LOGIN else Routes.SHELL

    NavHost(navController = nav, startDestination = start) {
        composable(Routes.LOGIN) { LoginScreen(sessionVm) }
        composable(Routes.SHELL) { Shell(onLogout = { sessionVm.signOut() }) }
    }

    LaunchedEffect(user) {
        if (user == null) {
            nav.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } }
        } else {
            nav.navigate(Routes.SHELL) { popUpTo(0) { inclusive = true } }
        }
    }
}
