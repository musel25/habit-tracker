package com.musel.habittracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits WHERE archived = 0 ORDER BY sort_index ASC, created_at ASC")
    fun observeHabits(): Flow<List<Habit>>

    @Query("SELECT * FROM habits WHERE id = :id LIMIT 1")
    fun observeHabit(id: Long): Flow<Habit?>

    @Query("SELECT * FROM habit_completions WHERE epoch_day >= :startInclusive AND epoch_day <= :endInclusive")
    fun observeCompletionsInRange(startInclusive: Long, endInclusive: Long): Flow<List<HabitCompletion>>

    @Query("SELECT * FROM habit_completions WHERE habit_id = :habitId ORDER BY epoch_day DESC")
    fun observeCompletionsFor(habitId: Long): Flow<List<HabitCompletion>>

    @Query("SELECT epoch_day FROM habit_completions WHERE habit_id = :habitId ORDER BY epoch_day DESC")
    suspend fun completionDaysFor(habitId: Long): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: Habit): Long

    @Update
    suspend fun updateHabit(habit: Habit)

    @Delete
    suspend fun deleteHabit(habit: Habit)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletion(completion: HabitCompletion)

    @Query("DELETE FROM habit_completions WHERE habit_id = :habitId AND epoch_day = :epochDay")
    suspend fun deleteCompletion(habitId: Long, epochDay: Long)

    @Query("SELECT EXISTS(SELECT 1 FROM habit_completions WHERE habit_id = :habitId AND epoch_day = :epochDay)")
    suspend fun isCompleted(habitId: Long, epochDay: Long): Boolean

    @Query("SELECT COALESCE(MAX(sort_index), -1) FROM habits")
    suspend fun maxSortIndex(): Int
}
