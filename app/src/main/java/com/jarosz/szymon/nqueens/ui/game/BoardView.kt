package com.jarosz.szymon.nqueens.ui.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.jarosz.szymon.nqueens.R
import com.jarosz.szymon.nqueens.ui.theme.NQueensTheme

@Composable
fun BoardView(state: GameState, onPlaceQueen: (cell: Cell) -> Unit) {
    val boardPadding = 2

    BoxWithConstraints(
            modifier = Modifier
                    .padding(16.dp)
                    .border(2.dp, MaterialTheme.colorScheme.outlineVariant, shape = AbsoluteRoundedCornerShape(8.dp))
    ) {
        val size = minOf(maxWidth, maxHeight)

        Box(
                modifier = Modifier
                        .padding(boardPadding.dp)
                        .size(size - boardPadding.dp * 2),
                contentAlignment = Alignment.Center,
        ) {
            Column {
                state.board.chunked(state.boardSize).forEach { row ->
                    Row(
                            horizontalArrangement = Arrangement.Center,
                    ) {
                        row.forEach { cell ->
                            BoardCell(
                                    cell,
                                    (size - boardPadding.dp * 2) / state.boardSize,
                                    onClick = { onPlaceQueen(cell) },
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BoardCell(
        cell: Cell,
        size: Dp,
        onClick: () -> Unit,
) {
    val row = cell.position.row
    val col = cell.position.col
    val isLightSquare = (row + col) % 2 == 0
    val cellColor = if (isLightSquare) MaterialTheme.colorScheme.surfaceContainerLowest else MaterialTheme.colorScheme.surfaceContainerHighest
    val queenColor by animateColorAsState(
            targetValue = if (cell.isConflict) MaterialTheme.colorScheme.error else
                MaterialTheme.colorScheme.primary,
            animationSpec = SpringSpec()
    )

    Box(
            modifier = Modifier
                    .background(cellColor)
                    .size(size)
                    .clickable { onClick() },
            contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
                visible = cell.hasQueen,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut()
        ) {
            Image(
                    modifier = Modifier
                            .size(size * 0.4f),
                    painter = painterResource(R.drawable.chess_queen),
                    colorFilter = ColorFilter.tint(
                            queenColor
                    ),
                    contentDescription = null,
            )
        }

        AnimatedBorder(
                size = size,
                isConflict = cell.isConflict,
                baseColor = cellColor
        )
    }
}

@Composable
fun AnimatedBorder(
        size: Dp,
        isConflict: Boolean,
        baseColor: Color,
) {
    val borderColor by animateColorAsState(
            targetValue = if (isConflict) MaterialTheme.colorScheme.error else baseColor,
            label = "BorderColorAnimation",
            animationSpec = SpringSpec()
    )

    Box(
            modifier = Modifier
                    .border(
                            width = 2.dp,
                            color = borderColor,
                            shape = AbsoluteRoundedCornerShape(size = 8.dp)
                    )
                    .size(size)
    )
}

@Preview
@Composable
private fun GameScreenPreview(
        @PreviewParameter(GameStatePreviewProvider::class) state: GameState
) {
    NQueensTheme {
        BoardView(
                state = state,
                onPlaceQueen = {}
        )
    }
}
