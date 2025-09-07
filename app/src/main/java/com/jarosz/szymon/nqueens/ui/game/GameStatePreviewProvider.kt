package com.jarosz.szymon.nqueens.ui.game

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.jarosz.szymon.nqueens.board.PieceType
import com.jarosz.szymon.nqueens.board.Position

class GameStatePreviewProvider : PreviewParameterProvider<GameState> {
    override val values: Sequence<GameState> = sequenceOf(
        GameState(
            4,
            generateBoardWithQueensAndConflicts(
                4, listOf(
                    Position(0, 1),
                    Position(1, 3),
                    Position(2, 0),
                    Position(3, 3),
                ), listOf(
                    Position(1, 3),
                    Position(3, 3),

                    )
            )
        ),
    )

    private fun generateBoardWithQueensAndConflicts(
        boardSize: Int,
        queenPositions: List<Position>,
        conflictPositions: List<Position>
    ): List<Cell> {
        return List(boardSize * boardSize) { index ->
            val row = index / boardSize
            val col = index % boardSize
            val pos = Position(row, col)
            Cell(
                position = pos,
                hasQueen = queenPositions.any({ it == pos }),
                isConflict = conflictPositions.contains(pos) && queenPositions.contains(
                    pos
                )
            )
        }
    }
}
