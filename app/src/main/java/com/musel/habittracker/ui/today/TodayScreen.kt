package com.musel.habittracker.ui.today

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.musel.habittracker.ui.components.HabitRow
import com.musel.habittracker.ui.components.dayName
import com.musel.habittracker.ui.components.prettyDate

@Composable
fun TodayScreen(
    onAddHabit: () -> Unit,
    onOpenHabit: (Long) -> Unit,
    viewModel: TodayViewModel = viewModel(factory = TodayViewModel.Factory),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddHabit,
                shape = RoundedCornerShape(20.dp),
                containerColor = MaterialTheme.colorScheme.onBackground,
                contentColor = MaterialTheme.colorScheme.background,
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add habit")
            }
        },
    ) { inner ->
        if (state.habits.isEmpty() && !state.loading) {
            EmptyState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(inner),
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(
                    top = inner.calculateTopPadding() + 8.dp,
                    bottom = inner.calculateBottomPadding() + 96.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                item { Header(state) }
                items(state.habits, key = { it.habit.id }) { habit ->
                    HabitRow(
                        habit = habit.habit,
                        completedToday = habit.completedToday,
                        streak = habit.streak,
                        onToggle = { viewModel.toggle(habit.habit.id) },
                        onOpen = { onOpenHabit(habit.habit.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun Header(state: TodayUiState) {
    Column(
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp),
    ) {
        Text(
            text = state.date.dayName(),
            style = MaterialTheme.typography.displaySmall,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = state.date.prettyDate(),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (state.total > 0) {
            Spacer(Modifier.height(16.dp))
            val left = state.total - state.completed
            val sub = when {
                state.completed == state.total -> "All done"
                state.completed == 0 -> "${state.total} to do"
                left == 1 -> "${state.completed} done · 1 to go"
                else -> "${state.completed} done · $left to go"
            }
            Text(
                text = sub,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "No habits yet",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Tap + to start tracking one.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
