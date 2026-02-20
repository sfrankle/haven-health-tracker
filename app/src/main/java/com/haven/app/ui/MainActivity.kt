package com.haven.app.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.haven.app.ui.navigation.HavenDestination
import com.haven.app.ui.theme.HavenTheme
import com.haven.app.ui.placeholder.PlaceholderScreen
import com.haven.app.ui.logging.LoggingRoute
import com.haven.app.ui.tend.TendScreen
import com.haven.app.ui.trace.TraceScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HavenTheme {
                HavenApp()
            }
        }
    }
}

@Composable
fun HavenApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                HavenDestination.entries.forEach { destination ->
                    NavigationBarItem(
                        icon = { Icon(destination.icon, contentDescription = destination.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == destination.route } == true,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = HavenDestination.Tend.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(HavenDestination.Tend.route) {
                TendScreen(
                    onEntryTypeClick = { entryType ->
                        navController.navigate("log/${entryType.id}")
                    }
                )
            }
            composable(HavenDestination.Trace.route) { TraceScreen() }
            composable(HavenDestination.Weave.route) { PlaceholderScreen("Weave") }
            composable(HavenDestination.Anchor.route) { PlaceholderScreen("Anchor") }
            composable(HavenDestination.Settings.route) { PlaceholderScreen("Settings") }
            composable(
                route = "log/{entryTypeId}",
                arguments = listOf(navArgument("entryTypeId") { type = NavType.LongType })
            ) { backStackEntry ->
                val entryTypeId = backStackEntry.arguments?.getLong("entryTypeId") ?: return@composable
                LoggingRoute(
                    entryTypeId = entryTypeId,
                    onSaved = { navController.popBackStack() },
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
