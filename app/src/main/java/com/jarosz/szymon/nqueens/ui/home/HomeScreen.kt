package com.jarosz.szymon.nqueens.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.util.fastRoundToInt

@Composable
fun HomeScreen(onStartGame: () -> Unit) {
    val viewModel = androidx.lifecycle.viewmodel.compose.viewModel<HomeViewModel>()
    val uiState by viewModel.homeState.collectAsState()

    Column(modifier = Modifier
            .fillMaxSize()
            .background(Color.Yellow),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Board size:")
            Text(uiState.boardSize.toString())
        }
        Slider(
                valueRange = 4f..20f, value = uiState.boardSize.toFloat(),
                onValueChange = { viewModel.updateBoardSize(it.fastRoundToInt()) }
        )
        Button(onClick = onStartGame) { Text("Start game") }
    }
}
