package com.musel.habittracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.musel.habittracker.ui.detail.HabitDetailScreen
import com.musel.habittracker.ui.editor.HabitEditorSheet
import com.musel.habittracker.ui.theme.HabitTrackerTheme
import com.musel.habittracker.ui.today.TodayScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            HabitTrackerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    AppRoot()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppRoot() {
    val nav = rememberNavController()
    var editorTarget by rememberSaveable { mutableStateOf<Long?>(null) }
    var editorOpen by rememberSaveable { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    fun openEditor(id: Long?) {
        editorTarget = id
        editorOpen = true
    }

    NavHost(navController = nav, startDestination = "today") {
        composable("today") {
            TodayScreen(
                onAddHabit = { openEditor(null) },
                onOpenHabit = { id -> nav.navigate("habit/$id") },
            )
        }
        composable(
            "habit/{id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType }),
        ) { entry ->
            val id = entry.arguments?.getLong("id") ?: 0L
            HabitDetailScreen(
                habitId = id,
                onBack = { nav.popBackStack() },
                onEdit = { hid -> openEditor(hid) },
            )
        }
    }

    if (editorOpen) {
        HabitEditorSheet(
            habitId = editorTarget,
            sheetState = sheetState,
            onDismiss = {
                scope.launch {
                    sheetState.hide()
                    editorOpen = false
                }
            },
        )
    }
}
