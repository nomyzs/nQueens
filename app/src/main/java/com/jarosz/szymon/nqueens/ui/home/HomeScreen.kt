package com.jarosz.szymon.nqueens.ui.home

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastRoundToInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jarosz.szymon.nqueens.ui.common.GameCard
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

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

                Text("N-Queens", style = MaterialTheme.typography.displaySmall)
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
fun GameSetup(state: HomeState, viewModel: HomeViewModel, onStartGame: (boardSize: Int) -> Unit) {
    GameCard {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Board size:")
                Text("${state.boardSize} x ${state.boardSize}")
            }
            Slider(
                    valueRange = 4f..20f, value = state.boardSize.toFloat(),
                    onValueChange = { viewModel.updateBoardSize(it.fastRoundToInt()) },
                    colors = SliderDefaults.colors(
                            thumbColor = Color.White,
                            activeTrackColor = Color.White,
                            inactiveTrackColor = Color.DarkGray,
                            activeTickColor = Color.White,
                            inactiveTickColor = Color.DarkGray,
                    ),
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
        Text("Best times:")
        LazyColumn {
            items(state.results.size) { index ->
                val result = state.results[index]
                Card(
                        Modifier.padding(vertical = 4.dp),
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

private fun Int.toBoardSizeFormat(): String = "${this}x${this}"

private fun Long.toDurationFormat(): String = "${this / 1000},${(this + 5) / 10 % 100}s"

private fun Long.toDateFormat(): String {
    val date = Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault())
    return date.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT))


}


