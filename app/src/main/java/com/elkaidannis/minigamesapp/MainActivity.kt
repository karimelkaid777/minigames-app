package com.elkaidannis.minigamesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.elkaidannis.minigamesapp.ui.home.HomeScreen
import com.elkaidannis.minigamesapp.ui.leaderboard.LeaderboardScreen
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
        composable<Home> {
            HomeScreen(
                onReactionClick  = { name -> navController.navigate(Reaction(name)) },
                onWordGameClick  = { name -> navController.navigate(WordGame(name)) },
                onLeaderboardClick = { navController.navigate(Leaderboard) }
            )
        }
        composable<Reaction> { backStackEntry ->
            val route = backStackEntry.toRoute<Reaction>()
            ReactionScreen(
                playerName  = route.playerName,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable<WordGame> { backStackEntry ->
            val route = backStackEntry.toRoute<WordGame>()
            WordGameScreen(
                playerName  = route.playerName,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable<Leaderboard> {
            LeaderboardScreen(onBackClick = { navController.popBackStack() })
        }
    }
}
