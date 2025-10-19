package dev.miguelehr.conversordedivisas.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dev.miguelehr.conversordedivisas.data.repo.AuthRepo
import dev.miguelehr.conversordedivisas.ui.screens.ConvertScreen
import dev.miguelehr.conversordedivisas.ui.screens.HistoryScreen
import dev.miguelehr.conversordedivisas.ui.screens.LoginScreen

sealed class Route(val r: String) {
    data object Login: Route("login")
    data object Convert: Route("convert")
    data object History: Route("history")
}

@Composable
fun AppNav(auth: AuthRepo = AuthRepo()) {
    val nav = rememberNavController()
    val start = if (auth.isLoggedIn()) Route.Convert.r else Route.Login.r
    NavHost(navController = nav, startDestination = start) {

        composable(Route.Login.r) {
            LoginScreen(
                onSuccess = {
                    nav.navigate(Route.Convert.r) {
                        popUpTo(nav.graph.findStartDestination().id) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Route.Convert.r) {
            ConvertScreen(
                onOpenHistory = { nav.navigate(Route.History.r) },
                onLogout = {
                    auth.signOut()
                    nav.navigate(Route.Login.r) {
                        popUpTo(nav.graph.findStartDestination().id) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Route.History.r) {
            HistoryScreen()
        }
    }
}