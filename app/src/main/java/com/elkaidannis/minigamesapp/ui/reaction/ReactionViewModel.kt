package com.elkaidannis.minigamesapp.ui.reaction

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.elkaidannis.minigamesapp.data.AppDatabase
import com.elkaidannis.minigamesapp.data.Score
import com.elkaidannis.minigamesapp.data.ScoreRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.random.Random

private const val TICK_INTERVAL_MS = 10L

private const val MIN_TARGET_TIME_MS = 5_000L
private const val MAX_TARGET_TIME_MS = 20_000L

private const val MIN_STEP_MS = 5L
private const val MAX_STEP_MS = 24L

private const val MAX_START_OFFSET_ABOVE_TARGET_MS = 15_000L

private const val MIN_BLIND_THRESHOLD_MS = 1_000L
private const val MAX_BLIND_THRESHOLD_MS = 4_000L

private const val MIN_TICKS_BEFORE_SPEED_CHANGE = 150L
private const val MAX_TICKS_BEFORE_SPEED_CHANGE = 400L

private const val MOVING_UP = 1L
private const val MOVING_DOWN = -1L

data class ReactionUiState(
    val targetTimeMs: Long,
    val step: Long,
    val elapsedMs: Long,
    val blindThreshold: Long,
    val isRunning: Boolean = false,
    val isShowingResult: Boolean = false,
    val isBlind: Boolean = false
)

class ReactionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ScoreRepository(
        AppDatabase.getDatabase(application).scoreDao()
    )

    private val _uiState = MutableStateFlow(generateRound())
    val uiState: StateFlow<ReactionUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var playerName: String = ""

    fun startGame(playerName: String) {
        this.playerName = playerName
        _uiState.update { it.copy(isRunning = true) }
        timerJob = viewModelScope.launch { runTimer() }
    }

    fun stopTimer() {
        timerJob?.cancel()
        _uiState.update { it.copy(isRunning = false, isShowingResult = true) }
        saveScore()
    }

    fun reset() {
        timerJob?.cancel()
        _uiState.value = generateRound()
    }

    private fun saveScore() {
        val errorMs = abs(_uiState.value.elapsedMs - _uiState.value.targetTimeMs).toInt()
        viewModelScope.launch {
            repository.insertScore(
                Score(playerName = playerName, gameName = "Réaction", score = errorMs)
            )
        }
    }

    private suspend fun runTimer() {
        var ticksUntilSpeedChange = randomTicksBeforeSpeedChange()
        while (_uiState.value.isRunning) {
            delay(TICK_INTERVAL_MS)
            moveTimerOneTick()
            ticksUntilSpeedChange--
            if (ticksUntilSpeedChange <= 0L) {
                _uiState.update { it.copy(step = pickStepInSameDirection(it.step)) }
                ticksUntilSpeedChange = randomTicksBeforeSpeedChange()
            }
        }
    }

    private fun moveTimerOneTick() {
        _uiState.update { current ->
            val movedElapsedMs = current.elapsedMs + current.step
            val isCloseToTarget = abs(movedElapsedMs - current.targetTimeMs) < current.blindThreshold
            current.copy(
                elapsedMs = movedElapsedMs,
                isBlind = current.isBlind || isCloseToTarget
            )
        }
    }
}

private fun generateRound(): ReactionUiState {
    val step = randomStepMagnitude() * randomDirection()
    val targetTimeMs = randomLongInclusive(MIN_TARGET_TIME_MS, MAX_TARGET_TIME_MS)
    return ReactionUiState(
        targetTimeMs = targetTimeMs,
        step = step,
        elapsedMs = randomStartBeforeTarget(targetTimeMs, step),
        blindThreshold = randomLongInclusive(MIN_BLIND_THRESHOLD_MS, MAX_BLIND_THRESHOLD_MS)
    )
}

private fun randomStartBeforeTarget(targetTimeMs: Long, step: Long): Long =
    if (step > 0) Random.nextLong(0L, targetTimeMs)
    else randomLongInclusive(targetTimeMs, targetTimeMs + MAX_START_OFFSET_ABOVE_TARGET_MS)

private fun pickStepInSameDirection(currentStep: Long): Long {
    val direction = if (currentStep > 0) MOVING_UP else MOVING_DOWN
    return randomStepMagnitude() * direction
}

private fun randomStepMagnitude() = randomLongInclusive(MIN_STEP_MS, MAX_STEP_MS)

private fun randomDirection() = if (Random.nextBoolean()) MOVING_UP else MOVING_DOWN

private fun randomTicksBeforeSpeedChange() =
    randomLongInclusive(MIN_TICKS_BEFORE_SPEED_CHANGE, MAX_TICKS_BEFORE_SPEED_CHANGE)

private fun randomLongInclusive(minValue: Long, maxValue: Long) =
    Random.nextLong(minValue, maxValue + 1)
