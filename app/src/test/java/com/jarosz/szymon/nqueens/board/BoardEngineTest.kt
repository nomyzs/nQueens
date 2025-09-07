package com.jarosz.szymon.nqueens.board

import org.junit.Test

class BoardEngineTest {

    @Test
    fun `place queen`() {
        val boardEngine = BoardEngine(4)
        val position = Position(0, 0)
        boardEngine.addQueen(position)
        assert(boardEngine.hasQueen(position))
    }

    @Test
    fun `places only one queen at position`() {
        val boardEngine = BoardEngine(4)
        val position = Position(0, 0)
        boardEngine.addQueen(position)
        boardEngine.addQueen(position)
        boardEngine.addQueen(position)
        assert(boardEngine.hasQueen(position))
        assert(boardEngine.placedQueens.size == 1)
    }

    @Test
    fun `remove and remove queen`() {
        val boardEngine = BoardEngine(4)
        val position = Position(0, 0)
        boardEngine.addQueen(position)
        assert(boardEngine.hasQueen(position))

        boardEngine.removeQueen(position)
        assert(!boardEngine.hasQueen(position))
    }

    @Test
    fun `find potential moves`() {
        val boardEngine = BoardEngine(4)
        val position = Position(0, 0)
        val queen = Queen(position)
        val moves = queen.getMoves(boardEngine)
        val expectedMoves = listOf(
                Position(0, 1), Position(0, 2), Position(0, 3), // Right
                Position(1, 0), Position(2, 0), Position(3, 0), // Down
                Position(1, 1), Position(2, 2), Position(3, 3)  // Down-Right Diagonal
        )

        assert(moves.containsAll(expectedMoves) && expectedMoves.containsAll(moves))
    }

    @Test
    fun `detects row conflict`() {
        val boardEngine = BoardEngine(4)
        boardEngine.addQueen(Position(0, 0))
        boardEngine.addQueen(Position(0, 3))

        val conflicts = boardEngine.getConflicts()
        val expectedConflicts = listOf(Position(0, 0), Position(0, 3))

        assert(conflicts.containsAll(expectedConflicts) && expectedConflicts.containsAll(conflicts))
    }

    @Test
    fun `detects column conflict`() {
        val boardEngine = BoardEngine(4)
        boardEngine.addQueen(Position(0, 0))
        boardEngine.addQueen(Position(3, 0))

        val conflicts = boardEngine.getConflicts()
        val expectedConflicts = listOf(Position(0, 0), Position(3, 0))

        assert(conflicts.containsAll(expectedConflicts) && expectedConflicts.containsAll(conflicts))
    }

    @Test
    fun `detects diagonal conflict`() {
        val boardEngine = BoardEngine(4)
        boardEngine.addQueen(Position(0, 0))
        boardEngine.addQueen(Position(3, 3))

        val conflicts = boardEngine.getConflicts()
        val expectedConflicts = listOf(Position(0, 0), Position(3, 3))

        assert(conflicts.containsAll(expectedConflicts) && expectedConflicts.containsAll(conflicts))
    }

    @Test
    fun `no conflicts`() {
        val boardEngine = BoardEngine(4)
        boardEngine.addQueen(Position(0, 1))
        boardEngine.addQueen(Position(1, 3))
        boardEngine.addQueen(Position(2, 0))
        boardEngine.addQueen(Position(3, 2))

        val conflicts = boardEngine.getConflicts()
        val expectedConflicts = emptyList<Position>()

        assert(conflicts.containsAll(expectedConflicts) && expectedConflicts.containsAll(conflicts))
    }
}
