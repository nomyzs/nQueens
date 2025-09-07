package com.jarosz.szymon.nqueens.board

sealed class Movement {
    abstract fun getMoves(
        position: Position,
        boardEngine: BoardEngine
    ): List<Position>
}

class RookMovement() : Movement() {
    override fun getMoves(
        position: Position,
        boardEngine: BoardEngine
    ): List<Position> {
        val moves = mutableListOf<Position>()
        val maxSteps = boardEngine.size - 1

        for (direction in orthogonalDirections) {
            var step = 1
            var nextRow = position.row + direction.rowDelta
            var nextCol = position.col + direction.colDelta
            var nextPosition = Position(nextRow, nextCol)

            while (step <= maxSteps && boardEngine.isInside(nextPosition)) {
                moves.add(Position(nextRow, nextCol))
                step++
                nextRow += direction.rowDelta
                nextCol += direction.colDelta
                nextPosition = Position(nextRow, nextCol)
            }
        }

        return moves
    }
}

class QueenMovement : Movement() {
    override fun getMoves(
        position: Position,
        boardEngine: BoardEngine
    ): List<Position> {
        val directions = orthogonalDirections + diagonalDirections
        val moves = mutableListOf<Position>()
        val maxSteps = boardEngine.size - 1

        for (direction in directions) {
            var step = 1
            var nextRow = position.row + direction.rowDelta
            var nextCol = position.col + direction.colDelta
            var nextPosition = Position(nextRow, nextCol)

            while (step <= maxSteps && boardEngine.isInside(nextPosition)) {
                moves.add(Position(nextRow, nextCol))
                step++
                nextRow += direction.rowDelta
                nextCol += direction.colDelta
                nextPosition = Position(nextRow, nextCol)
            }
        }

        return moves
    }
}

val orthogonalDirections = listOf(
    Direction.UP,
    Direction.DOWN,
    Direction.LEFT,
    Direction.RIGHT,
)
val diagonalDirections = listOf(
    Direction.UP_LEFT,
    Direction.UP_RIGHT,
    Direction.DOWN_LEFT,
    Direction.DOWN_RIGHT,
)

enum class Direction(val rowDelta: Int, val colDelta: Int) {
    UP(-1, 0),
    DOWN(1, 0),
    LEFT(0, -1),
    RIGHT(0, 1),
    UP_LEFT(-1, -1),
    UP_RIGHT(-1, 1),
    DOWN_LEFT(1, -1),
    DOWN_RIGHT(1, 1)
}
