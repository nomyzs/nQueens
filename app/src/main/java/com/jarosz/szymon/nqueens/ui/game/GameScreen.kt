package com.jarosz.szymon.nqueens.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun GameScreen(boardSize: Int, onBack: () -> Boolean) {
    val viewModel = viewModel<GameViewModel>(factory = GameViewModelFactory(boardSize))
    val state by viewModel.state.collectAsState()

    if (state.showWinDialog) {
        AlertDialog(
                //TODO: remove dismiss request
                onDismissRequest = { viewModel.onWinDialogDismiss() },
                confirmButton = {
                    Button(onClick = {
                        viewModel.onWinDialogDismiss()
                        onBack()
                    }) { Text("Back") }
                },
                dismissButton = {
                    Button(
                            onClick = { viewModel.resetGame() }
                    ) { Text("Play Again") }
                },
                title = { Text("You Win!") },
        )
    }

    Box(Modifier
            .fillMaxSize(),
            contentAlignment = Alignment.Center) {
        Column {
            Text(
                    "Queens: ${state.placedQueensCount}/${boardSize}",
                    modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                    fontSize = 18.sp
            )

            LazyVerticalGrid(
                    columns = GridCells.Fixed(boardSize),
                    modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .padding(16.dp)
            ) {
                items(state.board.size) { index ->
                    val cell = state.board[index]
                    val row = cell.row
                    val col = cell.col
                    val isLightSquare = (row + col) % 2 == 0

                    Box(
                            modifier = Modifier
                                    .background(if (isLightSquare) Color.LightGray else Color.DarkGray)
                                    .aspectRatio(1f)
                                    .clickable { viewModel.placeQueen(cell) }
                    ) {
                        if (cell.hasQueen) {
                            Text(
                                    "♛",
                                    modifier = Modifier.align(Alignment.Center),
                                    color = if (cell.isConflict) Color.Red else if (isLightSquare) Color.Black else Color.White,
                                    fontSize = 24.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

class GameViewModelFactory(private val boardSize: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GameViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return GameViewModel(boardSize) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
