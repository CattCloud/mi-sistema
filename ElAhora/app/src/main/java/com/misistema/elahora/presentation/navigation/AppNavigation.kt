package com.misistema.elahora.presentation.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.misistema.elahora.presentation.home.HomeScreen
import com.misistema.elahora.presentation.settings.MarkdownReaderScreen
import com.misistema.elahora.presentation.settings.SettingsScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = Modifier.fillMaxSize()
    ) {
        composable("home") {
            HomeScreen(
                onNavigateToSettings = { navController.navigate("settings") }
            )
        }
        composable("settings") {
            SettingsScreen(
                onNavigateToMarkdown = { fileName ->
                    navController.navigate("markdown/$fileName")
                }
            )
        }
        composable("markdown/{fileName}") { backStackEntry ->
            val fileName = backStackEntry.arguments?.getString("fileName") ?: "error.md"
            MarkdownReaderScreen(fileName = fileName)
        }
    }
}
