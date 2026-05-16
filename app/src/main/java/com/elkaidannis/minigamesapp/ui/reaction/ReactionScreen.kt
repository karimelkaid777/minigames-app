package com.elkaidannis.minigamesapp.ui.reaction

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs

@Composable
fun ReactionScreen(onBackClick: () -> Unit) {
    var elapsedMs        by remember { mutableLongStateOf(0L) }
    var targetTimeMs     by remember { mutableLongStateOf(5_000L) }
    var step             by remember { mutableLongStateOf(10L) }
    var isRunning        by remember { mutableStateOf(false) }
    var isShowingResult  by remember { mutableStateOf(false) }

    fun restartGame() {
        elapsedMs       = 0L
        targetTimeMs    = 5_000L
        step            = 10L
        isRunning       = false
        isShowingResult = false
    }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
        if (isShowingResult) {
            ResultScreen(
                elapsedMs    = elapsedMs,
                targetTimeMs = targetTimeMs,
                onReplayClick = { restartGame() },
                onHomeClick   = onBackClick,
                modifier      = Modifier.padding(innerPadding)
            )
        } else {
            GameScreen(
                elapsedMs    = elapsedMs,
                targetTimeMs = targetTimeMs,
                step         = step,
                isRunning    = isRunning,
                onBackClick  = onBackClick,
                onStartClick = { isRunning = true },
                onStopClick  = { isRunning = false; isShowingResult = true },
                modifier     = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
private fun GameScreen(
    elapsedMs: Long,
    targetTimeMs: Long,
    step: Long,
    isRunning: Boolean,
    onBackClick: () -> Unit,
    onStartClick: () -> Unit,
    onStopClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        GameHeader(onBackClick = onBackClick)
        TargetCard(targetTimeMs = targetTimeMs, step = step)
        TimerDisplay(
            elapsedMs    = elapsedMs,
            targetTimeMs = targetTimeMs,
            isRunning    = isRunning,
            modifier     = Modifier.weight(1f)
        )
        StartStopButton(
            isRunning    = isRunning,
            onStartClick = onStartClick,
            onStopClick  = onStopClick
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun GameHeader(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier.padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp))
                .clickable { onBackClick() },
            contentAlignment = Alignment.Center
        ) {
            Text("←", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Jeu de réaction",
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun TargetCard(targetTimeMs: Long, step: Long) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Valeur cible", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    text = formatTimeDisplay(targetTimeMs),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            SpeedBadge(step = step)
        }
    }
}

@Composable
private fun SpeedBadge(step: Long) {
    Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
        Text(
            text = "× %.1f vitesse".format(abs(step).toFloat() / 10f),
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun TimerDisplay(
    elapsedMs: Long,
    targetTimeMs: Long,
    isRunning: Boolean,
    modifier: Modifier = Modifier
) {
    val progress = if (targetTimeMs > 0L) (elapsedMs.toFloat() / targetTimeMs.toFloat()).coerceIn(0f, 1f) else 0f

    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("TIMER", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = formatTimeDisplay(elapsedMs),
                fontSize = 56.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.Monospace,
                color = if (isRunning) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

@Composable
private fun StartStopButton(isRunning: Boolean, onStartClick: () -> Unit, onStopClick: () -> Unit) {
    if (!isRunning) {
        OutlinedButton(
            onClick = onStartClick,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline)
        ) {
            Text("Démarrer", fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)
        }
    } else {
        Button(
            onClick = onStopClick,
            modifier = Modifier.fillMaxWidth().height(54.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("Stop !", fontSize = 18.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
private fun ResultScreen(
    elapsedMs: Long,
    targetTimeMs: Long,
    onReplayClick: () -> Unit,
    onHomeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize().padding(horizontal = 16.dp)
    ) {
        ResultSummary(
            elapsedMs    = elapsedMs,
            targetTimeMs = targetTimeMs,
            modifier     = Modifier.weight(1f)
        )
        ResultActions(onReplayClick = onReplayClick, onHomeClick = onHomeClick)
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ResultSummary(elapsedMs: Long, targetTimeMs: Long, modifier: Modifier = Modifier) {
    val gapMs = abs(elapsedMs - targetTimeMs)
    val (emoji, feedbackMessage) = when {
        gapMs < 100  -> "🎯" to "Parfait !"
        gapMs < 500  -> "⭐" to "Très bien !"
        gapMs < 1000 -> "👍" to "Bien !"
        else         -> "😅" to "À améliorer"
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = emoji, fontSize = 36.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = feedbackMessage, fontSize = 20.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onBackground)
        Text(text = "Tu t'es arrêté à", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        GapCard(gapMs = gapMs)
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatisticCard(label = "Cible",   value = formatTimeDisplay(targetTimeMs), modifier = Modifier.weight(1f))
            StatisticCard(label = "Atteint", value = formatTimeDisplay(elapsedMs),    modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun GapCard(gapMs: Long) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("ÉCART", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                text = "+${formatTimeDisplay(gapMs)}",
                fontSize = 40.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.primary
            )
            Text("millisecondes", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun StatisticCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.Medium, fontFamily = FontFamily.Monospace, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
private fun ResultActions(onReplayClick: () -> Unit, onHomeClick: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Button(
            onClick = onReplayClick,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Rejouer", fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
        OutlinedButton(
            onClick = onHomeClick,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline)
        ) {
            Text("Retour à l'accueil", fontSize = 15.sp, color = MaterialTheme.colorScheme.onBackground)
        }
    }
}

private fun formatTimeDisplay(timeMs: Long): String {
    val timeAsString = timeMs.toString()
    return timeAsString.reversed().chunked(3).joinToString(" ").reversed()
}
