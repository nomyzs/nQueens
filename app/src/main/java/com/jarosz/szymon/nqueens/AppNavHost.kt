package com.jarosz.szymon.nqueens

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jarosz.szymon.nqueens.ui.game.GameScreen
import com.jarosz.szymon.nqueens.ui.game.GameScreenOutput
import com.jarosz.szymon.nqueens.ui.home.HomeScreen
import com.jarosz.szymon.nqueens.ui.home.HomeScreenOutput

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Game : Screen("game/{boardSize}") {
        fun createRoute(boardSize: Int) = "game/$boardSize"
    }
}

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(
            navController = navController, startDestination = Screen.Home.route,

            ) {
        val homeOutput = object : HomeScreenOutput {
            override fun onStartGame(boardSize: Int) =
                    navController.navigate(Screen.Game.createRoute(boardSize))
        }
        val gameOutput = object : GameScreenOutput {
            override fun onBack(): Boolean = navController.popBackStack()
        }

        composable(Screen.Home.route) {
            HomeScreen(homeOutput)
        }
        composable(
                Screen.Game.route,
                enterTransition = {
                    slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tween(TRANSITION_DURATION))
                },
                exitTransition = {
                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, tween(TRANSITION_DURATION))
                },
                arguments = listOf(navArgument("boardSize") { type = NavType.IntType }),
        ) { _ ->
            GameScreen(gameOutput)
        }
    }
}

const val TRANSITION_DURATION = 250

