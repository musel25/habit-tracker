package com.musel.habittracker.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.musel.habittracker.HabitTrackerApp
import com.musel.habittracker.data.HabitDetail
import com.musel.habittracker.data.HabitRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HabitDetailViewModel(
    private val habitId: Long,
    private val repository: HabitRepository,
) : ViewModel() {

    val state: StateFlow<HabitDetail?> =
        repository.detailFlow(habitId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun toggle(epochDay: Long) {
        viewModelScope.launch { repository.toggleDay(habitId, epochDay) }
    }

    fun delete(onDone: () -> Unit) {
        viewModelScope.launch {
            val cur = state.value?.habit ?: return@launch
            repository.deleteHabit(cur)
            onDone()
        }
    }

    companion object {
        fun factory(habitId: Long): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as HabitTrackerApp
                HabitDetailViewModel(habitId, app.repository)
            }
        }
    }
}
