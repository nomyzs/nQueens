package com.jarosz.szymon.nqueens.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.jarosz.szymon.nqueens.R

@Composable
fun AppLogo(modifier: Modifier?) {
    Image(
            painter = painterResource(id = R.drawable.chess_queen),
            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.primary),
            contentDescription = null,
            modifier = modifier ?: Modifier.size(24.dp)
    )
}
