
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
import dev.miguelehr.conversordedivisas.ui.screens.RegisterScreen

sealed class Route(val r: String) {
    data object Login : Route("login")
    data object Convert : Route("convert")
    data object History : Route("history")
    data object Register : Route("register")
}

@Composable
fun AppNav(auth: AuthRepo = AuthRepo()) {
    val nav = rememberNavController()
    val start = if (auth.isLoggedIn()) Route.Convert.r else Route.Login.r

    NavHost(navController = nav, startDestination = start) {

        // LOGIN
        composable(Route.Login.r) {
            LoginScreen(
                onSuccess = {
                    nav.navigate(Route.Convert.r) {
                        popUpTo(nav.graph.findStartDestination().id) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onGoToRegister = { nav.navigate(Route.Register.r) } // ⬅️ nuevo callback
            )
        }

        // CONVERT
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

        // HISTORY
        composable(Route.History.r) {
            HistoryScreen()
        }

        // REGISTER
        composable(Route.Register.r) {
            RegisterScreen(
                onRegistered = {
                    // vuelve a login limpio
                    nav.navigate(Route.Login.r) {
                        popUpTo(nav.graph.findStartDestination().id) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onCancel = { nav.popBackStack() } // volver a Login
            )
        }
    }
}