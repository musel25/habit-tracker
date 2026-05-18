package com.musel.habittracker.ui.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.musel.habittracker.HabitTrackerApp
import com.musel.habittracker.data.Habit
import com.musel.habittracker.data.HabitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class EditorState(
    val id: Long = 0,
    val name: String = "",
    val emoji: String = "",
    val colorIndex: Int = 0,
    val loaded: Boolean = false,
) {
    val canSave: Boolean get() = name.isNotBlank()
    val isEditing: Boolean get() = id != 0L
}

class HabitEditorViewModel(private val repository: HabitRepository) : ViewModel() {

    private val _state = MutableStateFlow(EditorState())
    val state: StateFlow<EditorState> = _state.asStateFlow()

    fun start(habitId: Long?) {
        if (habitId == null || habitId == 0L) {
            _state.value = EditorState(loaded = true)
            return
        }
        viewModelScope.launch {
            repository.detailFlow(habitId).collect { detail ->
                if (detail != null) {
                    val cur = _state.value
                    if (!cur.loaded || cur.id != detail.habit.id) {
                        _state.value = EditorState(
                            id = detail.habit.id,
                            name = detail.habit.name,
                            emoji = detail.habit.emoji.orEmpty(),
                            colorIndex = detail.habit.colorIndex,
                            loaded = true,
                        )
                    }
                }
            }
        }
    }

    fun onName(value: String) { _state.value = _state.value.copy(name = value) }
    fun onEmoji(value: String) { _state.value = _state.value.copy(emoji = value.take(4)) }
    fun onColor(index: Int) { _state.value = _state.value.copy(colorIndex = index) }

    fun save(onDone: () -> Unit) {
        val s = _state.value
        if (!s.canSave) return
        viewModelScope.launch {
            repository.upsertHabit(
                Habit(
                    id = s.id,
                    name = s.name.trim(),
                    emoji = s.emoji.trim().ifBlank { null },
                    colorIndex = s.colorIndex,
                )
            )
            onDone()
        }
    }

    fun delete(onDone: () -> Unit) {
        val s = _state.value
        if (s.id == 0L) { onDone(); return }
        viewModelScope.launch {
            repository.deleteHabit(
                Habit(id = s.id, name = s.name, emoji = s.emoji.ifBlank { null }, colorIndex = s.colorIndex)
            )
            onDone()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as HabitTrackerApp
                HabitEditorViewModel(app.repository)
            }
        }
    }
}
