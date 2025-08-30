package com.jarosz.szymon.nqueens.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Game : Screen("home/game")
}


@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(onStartGame = { navController.navigate(Screen.Game.route) })
        }
        composable(
                Screen.Game.route,
        ) { _ ->
            GameScreen()
        }
    }
}

@Composable
fun HomeScreen(onStartGame: () -> Unit) {
    Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.Yellow),
            contentAlignment = Alignment.Center) {
        Button(onClick = onStartGame) { Text("Start game") }
    }
}

@Composable
fun GameScreen() {
    Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.Blue)) {}
}
