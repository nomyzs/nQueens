package com.jarosz.szymon.nqueens.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastRoundToInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jarosz.szymon.nqueens.ui.common.GameCard
import com.jarosz.szymon.nqueens.ui.common.toBoardSizeFormat
import com.jarosz.szymon.nqueens.ui.common.toDateFormat
import com.jarosz.szymon.nqueens.ui.common.toDurationFormat
import com.jarosz.szymon.nqueens.ui.game.QUEEN

@Composable
fun HomeScreen(viewModel: HomeViewModel = hiltViewModel(), onStartGame: (boardSize: Int) -> Unit) {
    val state by viewModel.homeState.collectAsStateWithLifecycle()

    Scaffold { innerPadding ->
        Box(modifier = Modifier.padding(16.dp)) {
            Column(
                    modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(QUEEN, style = MaterialTheme.typography.displaySmall, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(4.dp))
                    Text("N-Queens", style = MaterialTheme.typography.displaySmall)
                }
                Text("Place queens without conflicts", style = MaterialTheme.typography.labelSmall)
                Spacer(Modifier.height(16.dp))
                GameSetup(state, viewModel, onStartGame)
                Spacer(Modifier.height(16.dp))
                GameCard({ BestTimes(state) })
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameSetup(state: HomeState, viewModel: HomeViewModel, onStartGame: (boardSize: Int) -> Unit) { // todo duplicated listener (interface -> viewmodel)
    GameCard {
        Column {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Board size:", style = MaterialTheme.typography.titleMedium)
                Text("${state.boardSize} x ${state.boardSize}", style = MaterialTheme.typography.titleMedium)
            }
            Slider(
                    valueRange = 4f..20f, value = state.boardSize.toFloat(),
                    onValueChange = { viewModel.updateBoardSize(it.fastRoundToInt()) },
                    track = { sliderState ->
                        SliderDefaults.Track(
                                sliderState,
                                modifier = Modifier.height(8.dp),
                                thumbTrackGapSize = 0.dp,
                                drawTick = { _, _ -> },
                                drawStopIndicator = { _ -> },
                        )
                    },
                    thumb = {
                        Box(
                                modifier = Modifier
                                        .size(24.dp)
                                        .padding(0.dp)
                                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                        )
                    },

                    )
            Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onStartGame(state.boardSize) }
            ) { Text("Start game") }
        }
    }
}

@Composable
private fun BestTimes(state: HomeState) {
    Column {
        Text("Best times:", style = MaterialTheme.typography.titleMedium)
        LazyColumn {
            items(state.results.size) { index ->
                val result = state.results[index]
                Card(
                        Modifier.padding(vertical = 4.dp),
                        border = if(state.boardSize == result.boardSize) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null,
                        shape = MaterialTheme.shapes.medium) {
                    ListItem(
                            { Text(result.timestamp.toDateFormat()) },
                            leadingContent = { Text(result.boardSize.toBoardSizeFormat(), fontWeight = FontWeight.Bold) },
                            trailingContent = { Text(result.timeMillis.toDurationFormat(), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold) }
                    )
                }
            }
        }
    }
}




