package com.elkaidannis.minigamesapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.elkaidannis.minigamesapp.ui.home.HomeScreen
import com.elkaidannis.minigamesapp.ui.reaction.ReactionScreen
import com.elkaidannis.minigamesapp.ui.theme.MiniGamesAppTheme

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
    var isPlaying by remember { mutableStateOf(false) }

    if (isPlaying) {
        ReactionScreen(onBackClick = { isPlaying = false })
    } else {
        HomeScreen(onPlayClick = { isPlaying = true })
    }
}