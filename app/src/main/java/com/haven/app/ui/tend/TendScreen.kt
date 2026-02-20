package com.haven.app.ui.tend

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.haven.app.data.entity.EntryTypeEntity
import com.haven.app.ui.common.GradientScaffold
import com.haven.app.ui.common.entryTypeIcon
import com.haven.app.ui.navigation.HavenDestination
import com.haven.app.ui.theme.entryTypeGradient
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
                .height(360.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.22f),
                            Color.Transparent
                        ),
                        radius = 700f
                    )
                )
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(40.dp))
                Text(
                    text = "logging",
                    style = MaterialTheme.typography.labelMedium.copy(
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.38f)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "What would you like to log?",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Normal
                    ),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f)
                )
                Spacer(modifier = Modifier.height(32.dp))
            }

            items(entryTypes, key = { it.id }) { entryType ->
                EntryTypeCard(
                    entryType = entryType,
                    onClick = { onEntryTypeClick(entryType) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun EntryTypeCard(
    entryType: EntryTypeEntity,
    onClick: () -> Unit
) {
    val icon = entryTypeIcon(entryType.icon)
    val gradient = entryTypeGradient(entryType.icon)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(92.dp)
            .clip(CardShape)
            .background(gradient)
            .clickable { onClick() }
    ) {
        // Soft luminous highlight along the top edge — adds dimensional depth
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.20f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Oversized watermark icon — atmospheric depth layer
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .size(108.dp)
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp),
            tint = Color.White.copy(alpha = 0.15f)
        )

        // Foreground: icon + label, left-aligned
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 24.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = entryType.name,
                modifier = Modifier.size(34.dp),
                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.70f)
            )
            Spacer(modifier = Modifier.width(18.dp))
            Text(
                text = entryType.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Normal),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f)
            )
        }
    }
}
