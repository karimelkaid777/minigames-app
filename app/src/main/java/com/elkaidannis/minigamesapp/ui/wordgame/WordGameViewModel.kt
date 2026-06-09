package com.elkaidannis.minigamesapp.ui.wordgame

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val GAME_DURATION_SECONDS = 60
private const val EXTRA_LETTERS_COUNT = 3
private const val ONE_SECOND_MS = 1_000L
private const val HINT_PENALTY = 1

class WordGameViewModel : ViewModel() {

    enum class Phase { PLAYING, GAME_OVER }

    data class Cell(
        val char: Char,
        val isSelected: Boolean = false
    )

    data class UiState(
        val phase: Phase = Phase.PLAYING,
        val grid: List<Cell> = emptyList(),
        val selectedIndices: List<Int> = emptyList(),
        val score: Int = 0,
        val remainingSeconds: Int = 0,
        val wordLength: Int = 0,
        val hintLetter: Char? = null,
        val bestScore: Int = 0
    ) {
        val typedLetters: String
            get() = selectedIndices.joinToString("") { index -> grid[index].char.toString() }
    }

    private val wordList = listOf(
        "SOLEIL", "MAISON", "JARDIN", "CHEMIN", "BOUTON",
        "MIROIR", "PLANTE", "CARTON", "FUSEAU", "CITRON",
        "VIOLON", "RAPIDE", "BLOQUE", "MOUTON", "GATEAU"
    )

    private var hiddenWord: String = ""
    private var timerJob: Job? = null

    private val _uiState =
        MutableStateFlow(buildRoundWithNewWord(score = 0, remainingSeconds = GAME_DURATION_SECONDS))
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun startGame() {
        timerJob?.cancel()
        _uiState.value = buildRoundWithNewWord(score = 0, remainingSeconds = GAME_DURATION_SECONDS)
        startCountdown()
    }

    fun selectCell(index: Int) {
        _uiState.update { current ->
            if (current.grid[index].isSelected) return@update current
            current.copy(
                grid = current.grid.markCellSelected(index, isSelected = true),
                selectedIndices = current.selectedIndices + index
            )
        }
    }

    fun eraseLast() {
        _uiState.update { current ->
            val lastIndex = current.selectedIndices.lastOrNull() ?: return@update current
            current.copy(
                grid = current.grid.markCellSelected(lastIndex, isSelected = false),
                selectedIndices = current.selectedIndices.dropLast(1)
            )
        }
    }

    fun validate() {
        val current = _uiState.value
        if (current.typedLetters == hiddenWord) {
            _uiState.value = buildRoundWithNewWord(score = current.score + 1, remainingSeconds = current.remainingSeconds)
        } else {
            clearSelection()
        }
    }

    fun pass() {
        val current = _uiState.value
        _uiState.value = buildRoundWithNewWord(score = current.score, remainingSeconds = current.remainingSeconds)
    }

    fun useHint() {
        _uiState.update { current ->
            if (current.hintLetter != null) return@update current
            current.copy(
                hintLetter = hiddenWord.first(),
                score = (current.score - HINT_PENALTY).coerceAtLeast(0)
            )
        }
    }

    fun reset() {
        startGame()
    }

    private fun startCountdown() {
        timerJob = viewModelScope.launch {
            while (_uiState.value.remainingSeconds > 0) {
                delay(ONE_SECOND_MS)
                _uiState.update { it.copy(remainingSeconds = it.remainingSeconds - 1) }
            }
            endGame()
        }
    }

    private fun endGame() {
        sessionBestScore = maxOf(sessionBestScore, _uiState.value.score)
        _uiState.update { it.copy(phase = Phase.GAME_OVER, bestScore = sessionBestScore) }
    }

    private fun clearSelection() {
        _uiState.update { current ->
            current.copy(
                grid = current.grid.map { it.copy(isSelected = false) },
                selectedIndices = emptyList()
            )
        }
    }

    private fun buildRoundWithNewWord(score: Int, remainingSeconds: Int): UiState {
        hiddenWord = wordList.random()
        return UiState(
            phase = Phase.PLAYING,
            grid = buildGrid(hiddenWord),
            score = score,
            remainingSeconds = remainingSeconds,
            wordLength = hiddenWord.length
        )
    }

    private fun buildGrid(word: String): List<Cell> {
        val extraLetters = List(EXTRA_LETTERS_COUNT) { randomLetter() }
        val shuffledLetters = (word.toList() + extraLetters).shuffled()
        return shuffledLetters.map { letter -> Cell(char = letter) }
    }

    companion object {
        private var sessionBestScore = 0
    }
}

private fun List<WordGameViewModel.Cell>.markCellSelected(index: Int, isSelected: Boolean) =
    mapIndexed { position, cell ->
        if (position == index) cell.copy(isSelected = isSelected) else cell
    }

private fun randomLetter() = ('A'..'Z').random()
