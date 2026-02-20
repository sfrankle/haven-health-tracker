package com.haven.app.ui.placeholder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.haven.app.ui.common.GradientScaffold

@Composable
fun PlaceholderScreen(title: String, gradient: Brush) {
    GradientScaffold(gradient = gradient) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$title — coming soon",
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
