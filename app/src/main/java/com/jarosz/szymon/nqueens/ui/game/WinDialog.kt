package com.jarosz.szymon.nqueens.ui.game

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.jarosz.szymon.nqueens.R
import com.jarosz.szymon.nqueens.ui.common.toBoardSizeFormat
import com.jarosz.szymon.nqueens.ui.theme.NQueensTheme
import kotlinx.coroutines.delay

@Composable
fun WinDialog(
    boardSize: Int,
    onDismiss: () -> Unit,
    onResetGame: () -> Unit,
    onGoHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog({}) {
        WinDialogBody(
            boardSize = boardSize,
            onResetGame = onResetGame,
            onGoHome = onGoHome,
            modifier = modifier
        )
    }
}

@Composable
private fun WinDialogBody(
    boardSize: Int,
    onResetGame: () -> Unit,
    onGoHome: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        tonalElevation = 8.dp,
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            var visible by remember { mutableStateOf(false) }
            var playConfetti by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                visible = true
                delay(100L)
                playConfetti = true
            }
            val composition by rememberLottieComposition(
                LottieCompositionSpec.RawRes(R.raw.confetti)
            )
            val progress by animateLottieCompositionAsState(
                composition,
                isPlaying = playConfetti,
            )

            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .padding(bottom = 24.dp)
                    .alpha(0.6f)
            )

            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .wrapContentHeight(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TrophyAnimation(visible)
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "You placed all queens!",
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    text = "No conflicts on a ${boardSize.toBoardSizeFormat()} board. Brilliant.",
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(onClick = onResetGame) {
                        Text("Play Again")
                    }
                    Button(onClick = onGoHome) {
                        Text("Home")
                    }
                }
            }
        }
    }
}

@Composable
private fun TrophyAnimation(visible: Boolean) {
    AnimatedVisibility(
        visible,
        enter = scaleIn(
            animationSpec = spring(
                stiffness = Spring.StiffnessMedium,
                dampingRatio = Spring.DampingRatioMediumBouncy
            )
        ),
        exit = scaleOut(),
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(
                    MaterialTheme.colorScheme.onSurface.copy(
                        alpha = 0.2f
                    ),
                    RoundedCornerShape(20.dp)
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text("🏆", fontSize = 32.sp)
        }
    }
}

@Preview
@Composable
private fun WinDialogPreview() {
    NQueensTheme {
        Scaffold { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                WinDialogBody(
                    boardSize = 8,
                    onResetGame = {},
                    onGoHome = {}
                )
            }
        }
    }
}
