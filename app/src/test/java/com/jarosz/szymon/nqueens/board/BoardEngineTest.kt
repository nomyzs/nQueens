package com.jarosz.szymon.nqueens.board

import org.junit.Test

class BoardEngineTest {

    @Test
    fun `place queen`() {
        val boardEngine = SimpleBoardEngine(4)
        val position = Position(0, 0)
        boardEngine.addPiece(Piece(position, PieceType.QUEEN))
        assert(boardEngine.hasPiece(position))
    }

    @Test
    fun `places only one queen at position`() {
        val boardEngine = SimpleBoardEngine(4)
        val position = Position(0, 0)
        boardEngine.addPiece(Piece(position, PieceType.QUEEN))
        boardEngine.addPiece(Piece(position, PieceType.QUEEN))
        boardEngine.addPiece(Piece(position, PieceType.QUEEN))
        assert(boardEngine.hasPiece(position))
        assert(boardEngine.placedPieces.size == 1)
    }

    @Test
    fun `remove and remove queen`() {
        val boardEngine = SimpleBoardEngine(4)
        val position = Position(0, 0)
        boardEngine.addPiece(Piece(position, PieceType.QUEEN))
        assert(boardEngine.hasPiece(position))

        boardEngine.removePiece(position)
        assert(!boardEngine.hasPiece(position))
    }

    @Test
    fun `find potential moves`() {
        val boardEngine = SimpleBoardEngine(4)
        val position = Position(0, 0)
        val queen = Piece(position, PieceType.QUEEN)
        val moves = queen.getMoves(boardEngine)
        val expectedMoves = listOf(
            // Right:
            Position(0, 1),
            Position(0, 2),
            Position(0, 3),
            // Down:
            Position(1, 0),
            Position(2, 0),
            Position(3, 0),
            // Down-Right Diagonal:
            Position(1, 1),
            Position(2, 2),
            Position(3, 3)
        )

        assert(
            moves.containsAll(expectedMoves) && expectedMoves.containsAll(
                moves
            )
        )
    }

    @Test
    fun `detects row conflict`() {
        val boardEngine = SimpleBoardEngine(4)
        boardEngine.addPiece(Piece(Position(0, 0), PieceType.QUEEN))
        boardEngine.addPiece(Piece(Position(0, 3), PieceType.QUEEN))

        val conflicts = boardEngine.getConflicts()
        val expectedConflicts = listOf(Position(0, 0), Position(0, 3))

        assert(
            conflicts.containsAll(expectedConflicts) && expectedConflicts.containsAll(
                conflicts
            )
        )
    }

    @Test
    fun `detects column conflict`() {
        val boardEngine = SimpleBoardEngine(4)
        boardEngine.addPiece(Piece(Position(0, 0), PieceType.QUEEN))
        boardEngine.addPiece(Piece(Position(3, 0), PieceType.QUEEN))

        val conflicts = boardEngine.getConflicts()
        val expectedConflicts = listOf(Position(0, 0), Position(3, 0))

        assert(
            conflicts.containsAll(expectedConflicts) && expectedConflicts.containsAll(
                conflicts
            )
        )
    }

    @Test
    fun `detects diagonal conflict`() {
        val boardEngine = SimpleBoardEngine(4)
        boardEngine.addPiece(Piece(Position(0, 0), PieceType.QUEEN))
        boardEngine.addPiece(Piece(Position(3, 3), PieceType.QUEEN))

        val conflicts = boardEngine.getConflicts()
        val expectedConflicts = listOf(Position(0, 0), Position(3, 3))

        assert(
            conflicts.containsAll(expectedConflicts) && expectedConflicts.containsAll(
                conflicts
            )
        )
    }

    @Test
    fun `no conflicts`() {
        val boardEngine = SimpleBoardEngine(4)
        boardEngine.addPiece(Piece(Position(0, 1), PieceType.QUEEN))
        boardEngine.addPiece(Piece(Position(1, 3), PieceType.QUEEN))
        boardEngine.addPiece(Piece(Position(2, 0), PieceType.QUEEN))
        boardEngine.addPiece(Piece(Position(3, 2), PieceType.QUEEN))

        val conflicts = boardEngine.getConflicts()
        val expectedConflicts = emptyList<Position>()

        assert(
            conflicts.containsAll(expectedConflicts) && expectedConflicts.containsAll(
                conflicts
            )
        )
    }
}
