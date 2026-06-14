package com.elkaidannis.minigamesapp.ui.leaderboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.elkaidannis.minigamesapp.data.AppDatabase
import com.elkaidannis.minigamesapp.data.Score
import com.elkaidannis.minigamesapp.data.ScoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class GameFilter(val label: String, val gameName: String?) {
    ALL("Tous", null),
    REACTION("Réaction", "Réaction"),
    WORD_GAME("Mot caché", "Mot caché")
}

data class LeaderboardUiState(
    val scores: List<Score> = emptyList(),
    val filter: GameFilter = GameFilter.ALL,
    val playerGameCount: Int = 0,
    val playerAverageScore: Float = 0f
)

class LeaderboardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ScoreRepository(
        AppDatabase.getDatabase(application).scoreDao()
    )

    private val _uiState = MutableStateFlow(LeaderboardUiState())
    val uiState: StateFlow<LeaderboardUiState> = _uiState.asStateFlow()

    init {
        loadScores()
    }

    fun setFilter(filter: GameFilter) {
        _uiState.update { it.copy(filter = filter) }
        loadScores()
    }

    fun loadPlayerStats(playerName: String) {
        viewModelScope.launch {
            val count = repository.getPlayerGameCount(playerName)
            val avg = repository.getPlayerAverageScore(playerName)
            _uiState.update { it.copy(playerGameCount = count, playerAverageScore = avg) }
        }
    }

    fun deleteAllScores() {
        viewModelScope.launch {
            repository.deleteAllScores()
            _uiState.update { it.copy(scores = emptyList(), playerGameCount = 0, playerAverageScore = 0f) }
        }
    }

    private fun loadScores() {
        viewModelScope.launch {
            val scores = when (val gameName = _uiState.value.filter.gameName) {
                null -> repository.getTopScores()
                else -> repository.getTopScoresByGame(gameName)
            }
            _uiState.update { it.copy(scores = scores) }
        }
    }
}
