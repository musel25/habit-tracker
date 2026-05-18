package com.musel.habittracker

import android.app.Application
import com.musel.habittracker.data.HabitDatabase
import com.musel.habittracker.data.HabitRepository

class HabitTrackerApp : Application() {
    val repository: HabitRepository by lazy { HabitRepository(HabitDatabase.get(this).habitDao()) }
}
