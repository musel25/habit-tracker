package com.musel.habittracker.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

data class HabitToday(
    val habit: Habit,
    val completedToday: Boolean,
    val streak: Int,
)

data class HabitDetail(
    val habit: Habit,
    val completedDays: Set<Long>,
    val currentStreak: Int,
    val longestStreak: Int,
    val totalCompletions: Int,
)

class HabitRepository(private val dao: HabitDao) {

    fun todayFlow(): Flow<List<HabitToday>> {
        val today = today().toEpochDays().toLong()
        return combine(
            dao.observeHabits(),
            dao.observeCompletionsInRange(today - 60, today),
        ) { habits, completions ->
            val byHabit = completions.groupBy { it.habitId }.mapValues { entry -> entry.value.map { it.epochDay }.toSet() }
            habits.map { habit ->
                val days = byHabit[habit.id].orEmpty()
                HabitToday(
                    habit = habit,
                    completedToday = today in days,
                    streak = currentStreakFrom(today, days),
                )
            }
        }
    }

    fun detailFlow(habitId: Long): Flow<HabitDetail?> {
        return combine(
            dao.observeHabit(habitId),
            dao.observeCompletionsFor(habitId),
        ) { habit, completions ->
            if (habit == null) return@combine null
            val days = completions.map { it.epochDay }.toSet()
            val today = today().toEpochDays().toLong()
            HabitDetail(
                habit = habit,
                completedDays = days,
                currentStreak = currentStreakFrom(today, days),
                longestStreak = longestStreak(days),
                totalCompletions = days.size,
            )
        }
    }

    suspend fun toggleToday(habitId: Long) {
        toggleDay(habitId, today().toEpochDays().toLong())
    }

    suspend fun toggleDay(habitId: Long, epochDay: Long) {
        if (dao.isCompleted(habitId, epochDay)) {
            dao.deleteCompletion(habitId, epochDay)
        } else {
            dao.insertCompletion(HabitCompletion(habitId, epochDay))
        }
    }

    suspend fun upsertHabit(habit: Habit): Long {
        return if (habit.id == 0L) {
            val nextSort = dao.maxSortIndex() + 1
            dao.insertHabit(habit.copy(sortIndex = nextSort))
        } else {
            dao.updateHabit(habit)
            habit.id
        }
    }

    suspend fun deleteHabit(habit: Habit) {
        dao.deleteHabit(habit)
    }

    private fun currentStreakFrom(today: Long, days: Set<Long>): Int {
        if (days.isEmpty()) return 0
        var start = if (today in days) today else today - 1
        if (start !in days) return 0
        var count = 0
        while (start in days) {
            count++
            start--
        }
        return count
    }

    private fun longestStreak(days: Set<Long>): Int {
        if (days.isEmpty()) return 0
        val sorted = days.sorted()
        var best = 1
        var run = 1
        for (i in 1 until sorted.size) {
            run = if (sorted[i] == sorted[i - 1] + 1) run + 1 else 1
            if (run > best) best = run
        }
        return best
    }

    companion object {
        fun today(): LocalDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    }
}
