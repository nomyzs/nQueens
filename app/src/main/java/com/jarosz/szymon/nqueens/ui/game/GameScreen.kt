package com.jarosz.szymon.nqueens.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun GameScreen(boardSize: Int, onBack: () -> Boolean, viewModel: GameViewModel = hiltViewModel()) {
    //TODO: check if collectAsStateWithLifecycle is better and solve dialog dismiss if yes
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
            Row(
                    modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Queens: ${state.placedQueensCount}/${boardSize}")
                Text("Time: ${state.time}s")
            }

            Board(state, { viewModel.placeQueen(it) })
        }
    }
}

@Composable
fun Board(state: GameState, onPlaceQueen: (cell: Cell) -> Unit) {
    LazyVerticalGrid(
            columns = GridCells.Fixed(state.boardSize),
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
                            .clickable { onPlaceQueen(cell) }
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
