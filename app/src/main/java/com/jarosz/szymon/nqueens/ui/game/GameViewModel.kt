package com.jarosz.szymon.nqueens.ui.game

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class GameViewModel(private val boardSize: Int) : ViewModel() {
    private val _state = MutableStateFlow(_initialState)
    val state: StateFlow<GameState> = _state

    private val _initialState: GameState
        get() = GameState(boardSize.generateBoard())
}

data class GameState(val board: List<Cell> = emptyList())

data class Cell(val row: Int, val col: Int)

private fun Int.generateBoard(): List<Cell> {
    return List(this * this) { index ->
        Cell(row = index / this, col = index % this)
    }
}
