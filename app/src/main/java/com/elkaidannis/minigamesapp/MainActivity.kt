package com.elkaidannis.minigamesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.elkaidannis.minigamesapp.ui.home.HomeScreen
import com.elkaidannis.minigamesapp.ui.reaction.ReactionScreen
import com.elkaidannis.minigamesapp.ui.theme.MiniGamesAppTheme
import com.elkaidannis.minigamesapp.ui.wordgame.WordGameScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MiniGamesAppTheme {
                MiniGamesApp()
            }
        }
    }
}

@Composable
fun MiniGamesApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Home) {
        composable<Home> { _ ->
            HomeScreen(
                onReactionClick = { navController.navigate(Reaction) },
                onWordGameClick = { navController.navigate(WordGame) }
            )
        }
        composable<Reaction> { _ ->
            ReactionScreen(onBackClick = { navController.popBackStack() })
        }
        composable<WordGame> { _ ->
            WordGameScreen(onBackClick = { navController.popBackStack() })
        }
    }
}
