package com.musel.habittracker.ui.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.musel.habittracker.HabitTrackerApp
import com.musel.habittracker.data.HabitRepository
import com.musel.habittracker.data.HabitToday
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

data class TodayUiState(
    val date: LocalDate = HabitRepository.today(),
    val habits: List<HabitToday> = emptyList(),
    val loading: Boolean = true,
) {
    val completed: Int get() = habits.count { it.completedToday }
    val total: Int get() = habits.size
}

class TodayViewModel(private val repository: HabitRepository) : ViewModel() {

    val state: StateFlow<TodayUiState> = repository.todayFlow()
        .map { TodayUiState(habits = it, loading = false) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), TodayUiState())

    fun toggle(habitId: Long) {
        viewModelScope.launch { repository.toggleToday(habitId) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as HabitTrackerApp
                TodayViewModel(app.repository)
            }
        }
    }
}
