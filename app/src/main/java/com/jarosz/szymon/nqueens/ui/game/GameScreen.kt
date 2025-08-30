package com.jarosz.szymon.nqueens.ui.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun GameScreen(boardSize: Int) {
    val viewModel = viewModel<GameViewModel>(factory = GameViewModelFactory(boardSize))
    val state by viewModel.state.collectAsStateWithLifecycle()

    Box(Modifier
            .fillMaxSize(),
            contentAlignment = Alignment.Center) {
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
                )
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
