package com.jarosz.szymon.nqueens.ui.common

import com.jarosz.szymon.nqueens.board.BoardEngine
import com.jarosz.szymon.nqueens.board.CellBasedBoardEngine
import com.jarosz.szymon.nqueens.board.Position
import com.jarosz.szymon.nqueens.board.BoardEngineImpl
import com.jarosz.szymon.nqueens.ui.game.Cell
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

fun Int.toBoardSizeFormat(): String = "${this}x${this}"

fun Long.toDurationFormat(): String =
    String.format(Locale.getDefault(), "%.2fs", this / 1000f)

fun Long.toDateFormat(): String {
    val date = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault())

    return date.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))
}

// [BoardEngine] extensions

fun BoardEngineImpl.toUIBoard(conflicts: List<Position>): List<Cell> {
    val cells = size.generateUIBoard()

    return cells.map {
        it.copy(
            hasQueen = placedPieces.find { piece -> piece.position == it.position } != null,
            isConflict = conflicts.contains(it.position),
        )
    }
}

fun CellBasedBoardEngine.toUIBoard(conflicts: List<Position>): List<Cell> {
    return boardCells.map(
        {
            Cell(
                position = it.position,
                hasQueen = it.piece != null,
                isConflict = conflicts.contains(it.position),
            )
        }
    )
}

fun BoardEngine.isGameCompleted(conflicts: List<Position>): Boolean =
    conflicts.isEmpty() && placedPieces.size == size

fun Int.generateUIBoard(): List<Cell> = List(this * this) { index ->
    Cell(Position(row = index / this, col = index % this))
}
