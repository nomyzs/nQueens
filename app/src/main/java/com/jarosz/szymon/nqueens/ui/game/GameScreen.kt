package com.jarosz.szymon.nqueens.ui.game

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.jarosz.szymon.nqueens.R
import com.jarosz.szymon.nqueens.ui.common.AppLogo
import com.jarosz.szymon.nqueens.ui.common.GameCard
import com.jarosz.szymon.nqueens.ui.common.toBoardSizeFormat
import com.jarosz.szymon.nqueens.ui.common.toDurationFormat
import com.jarosz.szymon.nqueens.ui.theme.NQueensTheme

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

    GameBody(state, output, viewModel::resetGame, { viewModel.placeQueen(it.position) })
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun GameTopBar(state: GameState, onBack: () -> Boolean, onResetGame: () -> Unit) {
    CenterAlignedTopAppBar(
            title = {
                Row {
                    AppLogo(modifier = Modifier.size(24.dp))
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
                IconButton(onClick = onResetGame) {
                    Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Reset"
                    )
                }
            }
    )
}

@Composable
private fun GameBody(
        state: GameState,
        output: GameScreenOutput,
        onResetGame: () -> Unit,
        onPlaceQueen: (Cell) -> Unit,
) {
    Scaffold(
            topBar = {
                GameTopBar(state, { output.onBack() }, onResetGame)
            }
    ) { innerPadding ->
        Box(
                Modifier
                        .padding(innerPadding)
                        .fillMaxSize(),
                contentAlignment = Alignment.TopCenter
        ) {

            Column(modifier = Modifier.padding(16.dp).fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                GameCard {
                    Row(
                            modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        HeaderItem(
                                "Queens:",
                                "${state.placedQueensCount}/${state.boardSize}",
                                R.drawable.chess_queen
                        )
                        HeaderItem(
                                "Time:", state.time.toDurationFormat(), R.drawable.clock,
                        )
                    }
                }
                BoardView(state, onPlaceQueen)
                Spacer(Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun HeaderItem(title: String, value: String, @DrawableRes iconResId: Int? = null) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            iconResId?.let {
                Image(
                        modifier = Modifier.size(16.dp),
                        painter = painterResource(iconResId),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                )
                Spacer(Modifier.size(4.dp))
            }
            Text(title, style = MaterialTheme.typography.labelSmall)
        }
        Text(value, style = MaterialTheme.typography.titleLarge)
    }
}


@Preview
@Composable
private fun GameScreenPreview(
        @PreviewParameter(GameStatePreviewProvider::class) state: GameState

) {
    NQueensTheme {
        GameBody(
                state = state,
                output = { false },
                onResetGame = {},
                onPlaceQueen = {}
        )
    }
}

fun interface GameScreenOutput {
    fun onBack(): Boolean
}
