package com.musel.habittracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.musel.habittracker.data.HabitRepository
import kotlinx.datetime.isoDayNumber

@Composable
fun HeatmapGrid(
    completedDays: Set<Long>,
    accent: Color,
    onToggleDay: (Long) -> Unit,
    weeks: Int = 12,
    modifier: Modifier = Modifier,
) {
    val today = HabitRepository.today()
    val todayEpoch = today.toEpochDays().toLong()
    val mondayOffset = today.dayOfWeek.isoDayNumber - 1
    val endOfWeekEpoch = todayEpoch + (6 - mondayOffset)
    val totalDays = weeks * 7
    val startEpoch = endOfWeekEpoch - totalDays + 1

    val labelColor = MaterialTheme.colorScheme.onSurfaceVariant
    val emptyCell = MaterialTheme.colorScheme.surfaceContainer

    Row(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            listOf("Mon", "", "Wed", "", "Fri", "", "Sun").forEach { lbl ->
                Text(
                    text = lbl,
                    style = MaterialTheme.typography.labelSmall,
                    color = labelColor,
                    modifier = Modifier.size(width = 28.dp, height = 16.dp),
                )
            }
        }
        Spacer(Modifier.width(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            for (w in 0 until weeks) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    for (d in 0..6) {
                        val cellEpoch = startEpoch + w * 7 + d
                        val isFuture = cellEpoch > todayEpoch
                        val done = cellEpoch in completedDays
                        val color = when {
                            isFuture -> Color.Transparent
                            done -> accent
                            else -> emptyCell
                        }
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(color)
                                .then(
                                    if (!isFuture) Modifier.clickable { onToggleDay(cellEpoch) }
                                    else Modifier
                                ),
                        )
                    }
                }
            }
        }
    }
}
