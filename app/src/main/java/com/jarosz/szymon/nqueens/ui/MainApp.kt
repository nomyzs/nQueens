package com.jarosz.szymon.nqueens.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jarosz.szymon.nqueens.ui.game.GameScreen
import com.jarosz.szymon.nqueens.ui.home.HomeScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Game : Screen("game/{boardSize}") {
        fun createRoute(boardSize: Int) = "game/$boardSize"
    }
}

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(onStartGame = { navController.navigate(Screen.Game.createRoute(it)) })
        }
        composable(
                Screen.Game.route,
                arguments = listOf(navArgument("boardSize") { type = NavType.IntType }),
        ) { backStackEntry ->
            val size = backStackEntry.arguments?.getInt("boardSize") ?: 4
            GameScreen(size, onBack = { navController.popBackStack() })
        }
    }
}
