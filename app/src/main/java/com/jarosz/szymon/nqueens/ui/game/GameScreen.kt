package com.jarosz.szymon.nqueens.ui.game

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.SpringSpec
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.AbsoluteRoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import com.jarosz.szymon.nqueens.ui.common.GameCard
import com.jarosz.szymon.nqueens.ui.common.toBoardSizeFormat
import com.jarosz.szymon.nqueens.ui.common.toDurationFormat

@Composable
fun GameScreen(output: GameScreenOutput, viewModel: GameViewModel = hiltViewModel()) {
    //TODO: check if collectAsStateWithLifecycle is better and solve dialog dismiss if yes
    val state by viewModel.state.collectAsState()

    if (state.showWinDialog) {
        AlertDialog(
                //TODO: remove dismiss request
                onDismissRequest = { viewModel.onWinDialogDismiss() },
                confirmButton = {
                    Button(onClick = {
                        viewModel.onWinDialogDismiss()
                        output.onBack()
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

    Scaffold(
            topBar = { GameTopBar(state, { output.onBack() }, viewModel) }
    ) { innerPadding ->
        Box(Modifier
                .padding(innerPadding)
                .fillMaxSize(),
                contentAlignment = Alignment.TopCenter) {

            Column(modifier = Modifier.padding(16.dp)) {
                GameCard {
                    Row(
                            modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        HeaderItem("Queens:", "${state.placedQueensCount}/${state.boardSize}", QUEEN)
                        HeaderItem("Time:", state.time.toDurationFormat(), CLOCK)
                    }
                }
                Board(state, { viewModel.placeQueen(it) })
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun GameTopBar(state: GameState, onBack: () -> Boolean, viewModel: GameViewModel) {
    CenterAlignedTopAppBar(
            title = {
                Row {
                    Icon(imageVector = Icons.Default.Home, contentDescription = "Logo")
                    Text(state.boardSize.toBoardSizeFormat())

                }
            },
            navigationIcon = {
                IconButton(onClick = { onBack() }) {
                    Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                    )
                }
            },
            actions = {
                IconButton(
                        onClick = { viewModel.resetGame() }
                ) {
                    Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Reset"
                    )
                }
            }
    )
}

@Composable
private fun HeaderItem(title: String, value: String, icon: String? = null) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row {
            icon?.let { Text(icon, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary) }
            Text(title, style = MaterialTheme.typography.labelSmall)
        }
        Text(value, style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
fun Board(state: GameState, onPlaceQueen: (cell: Cell) -> Unit) {
    LazyVerticalGrid(
            columns = GridCells.Fixed(state.boardSize),
            modifier = Modifier
                    .aspectRatio(1f)
                    .padding(16.dp)
    ) {
        items(state.board.size) { index ->
            val cell = state.board[index]
            val row = cell.row
            val col = cell.col
            val isLightSquare = (row + col) % 2 == 0

            AnimatedBorderCell(cell.isConflict, if (isLightSquare) Color.LightGray else Color.DarkGray) {
                Box(
                        modifier = Modifier
                                .background(if (isLightSquare) Color.LightGray else Color.DarkGray)
                                .aspectRatio(1f)
                                .clickable { onPlaceQueen(cell) }
                ) {
                    if (cell.hasQueen) {
                        Text(
                                QUEEN,
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

@Composable
fun AnimatedBorderCell(isConflict: Boolean, baseColor: Color, content: @Composable () -> Unit) {
    val borderColor by animateColorAsState(
            targetValue = if (isConflict) Color.Red else baseColor,
            label = "BorderColorAnimation",
            animationSpec = SpringSpec()
    )

    Box(
            modifier = Modifier
                    .border(width = 2.dp, color = borderColor, shape = AbsoluteRoundedCornerShape(size = 8.dp))
    ) {
        content()
    }
}

interface GameScreenOutput {
    fun onBack(): Boolean
}

//TODO: replace these with svgs
const val QUEEN = "\u265B"
const val CLOCK = "\uD83D\uDD53"
