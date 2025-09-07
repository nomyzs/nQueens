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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import com.jarosz.szymon.nqueens.ui.common.GameCard
import com.jarosz.szymon.nqueens.ui.common.toBoardSizeFormat
import com.jarosz.szymon.nqueens.ui.common.toDurationFormat
import com.jarosz.szymon.nqueens.ui.theme.NQueensTheme

@Composable
fun GameScreen(
    output: GameScreenOutput, viewModel: GameViewModel = hiltViewModel()
) {
    //TODO: check if collectAsStateWithLifecycle is better and solve dialog dismiss if yes
    val state by viewModel.state.collectAsState()

    GameBody(
        state,
        output,
        { viewModel.resetGame() },
        { viewModel.placeQueen(it.position) },
        { viewModel.winDialogDismiss() },
    )
}

@Composable
private fun GameBody(
    state: GameState,
    output: GameScreenOutput,
    onResetGame: () -> Unit,
    onPlaceQueen: (Cell) -> Unit,
    onWinDialogDismiss: () -> Unit,
) {
    Scaffold(
        topBar = {
            GameTopBar({ output.onBack() }, onResetGame)
        }) { innerPadding ->
        Box(
            Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                GameInfo(state)
                BoardView(state, onPlaceQueen)
                Spacer(Modifier.fillMaxWidth())
            }
            if (state.showWinDialog)
                WinDialog(
                    boardSize = state.boardSize,
                    onDismiss = onWinDialogDismiss,
                    onResetGame = onResetGame,
                    onGoHome = {
                        onWinDialogDismiss()
                        output.onBack()
                    },
                )
        }

    }
}


@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun GameTopBar(onBack: () -> Boolean, onResetGame: () -> Unit) {
    TopAppBar(title = {}, navigationIcon = {
        IconButton(onClick = { onBack() }) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back"
            )
        }
    }, actions = {
        IconButton(onClick = onResetGame) {
            Icon(
                imageVector = Icons.Filled.Refresh,
                contentDescription = "Reset"
            )
        }
    })
}

@Composable
private fun GameInfo(state: GameState) {
    GameCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
        ) {
            HeaderItem(
                title = "Queens:",
                modifier = Modifier.weight(1f),
                value = "${state.placedQueensCount}/${state.boardSize}",
                iconResId = R.drawable.chess_queen
            )
            HeaderItem(
                title = "Board size:",
                value = state.boardSize.toBoardSizeFormat(),
                iconResId = R.drawable.chess_board,
                modifier = Modifier.weight(1f),
            )
            HeaderItem(
                modifier = Modifier.weight(1f),
                title = "Time:",
                value = state.time.toDurationFormat(),
                iconResId = R.drawable.clock,
            )
        }
    }
}

@Composable
private fun HeaderItem(
    title: String,
    value: String,
    @DrawableRes iconResId: Int? = null,
    modifier: Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier
    ) {
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
        Spacer(Modifier.height(4.dp))
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
            onPlaceQueen = {},
            onWinDialogDismiss = {})
    }
}

fun interface GameScreenOutput {
    fun onBack(): Boolean
}
