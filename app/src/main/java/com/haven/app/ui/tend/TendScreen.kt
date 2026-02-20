package com.haven.app.ui.tend

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.haven.app.data.entity.EntryTypeEntity
import com.haven.app.ui.common.GradientScaffold
import com.haven.app.ui.common.entryTypeIcon
import com.haven.app.ui.navigation.HavenDestination
import com.haven.app.ui.theme.entryTypeTint
import com.haven.app.ui.theme.tabGradient

private val CardShape = RoundedCornerShape(28.dp)

@Composable
fun TendScreen(
    viewModel: TendViewModel = hiltViewModel(),
    onEntryTypeClick: (EntryTypeEntity) -> Unit = {}
) {
    val entryTypes by viewModel.entryTypes.collectAsState(initial = emptyList())

    GradientScaffold(gradient = tabGradient(HavenDestination.Tend)) {
        // Atmospheric bloom: soft radial white highlight in the upper portion of the screen
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.18f),
                            Color.Transparent
                        ),
                        radius = 600f
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "LOG",
                style = MaterialTheme.typography.labelMedium.copy(
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
            Text(
                text = "What would you like to log?",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(28.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(4.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                items(entryTypes, key = { it.id }) { entryType ->
                    EntryTypeCard(
                        entryType = entryType,
                        onClick = { onEntryTypeClick(entryType) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EntryTypeCard(
    entryType: EntryTypeEntity,
    onClick: () -> Unit
) {
    val icon = entryTypeIcon(entryType.icon)

    Surface(
        onClick = onClick,
        shape = CardShape,
        color = entryTypeTint(entryType.icon),
        shadowElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            // Ghosted watermark icon — decorative depth layer
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
                    .size(72.dp)
                    .align(Alignment.CenterEnd)
                    .padding(end = 12.dp),
                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.06f)
            )
            // Foreground content
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = icon,
                    contentDescription = entryType.name,
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = entryType.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
