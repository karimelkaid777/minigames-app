package com.elkaidannis.minigamesapp.ui.wordgame

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.elkaidannis.minigamesapp.ui.wordgame.WordGameViewModel.Cell
import com.elkaidannis.minigamesapp.ui.wordgame.WordGameViewModel.Phase
import com.elkaidannis.minigamesapp.ui.wordgame.WordGameViewModel.UiState

private const val GRID_SIDE = 3

@Composable
fun WordGameScreen(
    onBackClick: () -> Unit,
    viewModel: WordGameViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.startGame() }

    Scaffold(containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
        val screenModifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 16.dp)
        when (uiState.phase) {
            Phase.PLAYING -> PlayingContent(
                uiState = uiState,
                onCellClick = viewModel::selectCell,
                onEraseClick = viewModel::eraseLast,
                onValidateClick = viewModel::validate,
                onPassClick = viewModel::pass,
                onHintClick = viewModel::useHint,
                onBackClick = onBackClick,
                modifier = screenModifier
            )
            Phase.GAME_OVER -> GameOverContent(
                score = uiState.score,
                bestScore = uiState.bestScore,
                onReplayClick = viewModel::reset,
                onHomeClick = onBackClick,
                modifier = screenModifier
            )
        }
    }
}

@Composable
private fun PlayingContent(
    uiState: UiState,
    onCellClick: (Int) -> Unit,
    onEraseClick: () -> Unit,
    onValidateClick: () -> Unit,
    onPassClick: () -> Unit,
    onHintClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        WordGameHeader(onBackClick = onBackClick)
        Spacer(modifier = Modifier.height(12.dp))
        TimerAndScore(remainingSeconds = uiState.remainingSeconds, score = uiState.score)
        Spacer(modifier = Modifier.height(20.dp))
        WordPromptRow(
            wordLength = uiState.wordLength,
            hintLetter = uiState.hintLetter,
            onHintClick = onHintClick
        )
        Spacer(modifier = Modifier.height(10.dp))
        TypedZone(typedLetters = uiState.typedLetters, onEraseClick = onEraseClick)
        Spacer(modifier = Modifier.height(20.dp))
        LetterGrid(grid = uiState.grid, onCellClick = onCellClick)
        Spacer(modifier = Modifier.weight(1f))
        ActionButtons(onValidateClick = onValidateClick, onPassClick = onPassClick)
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun WordGameHeader(onBackClick: () -> Unit) {
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
            text = "Mot caché",
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun TimerAndScore(remainingSeconds: Int, score: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        LabeledValue(label = "TEMPS", value = "${remainingSeconds}s")
        LabeledValue(label = "MOTS TROUVÉS", value = score.toString())
    }
}

@Composable
private fun LabeledValue(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            text = value,
            fontSize = 28.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily.Monospace,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun WordPromptRow(wordLength: Int, hintLetter: Char?, onHintClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        WordPrompt(wordLength = wordLength, hintLetter = hintLetter)
        TextButton(onClick = onHintClick, enabled = hintLetter == null) {
            Text("Indice (-1)", fontSize = 14.sp)
        }
    }
}

@Composable
private fun WordPrompt(wordLength: Int, hintLetter: Char?) {
    val prompt = if (hintLetter != null) {
        "Mot de $wordLength lettres · commence par $hintLetter"
    } else {
        "Mot de $wordLength lettres"
    }
    Text(
        text = prompt,
        fontSize = 15.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
private fun TypedZone(typedLetters: String, onEraseClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = typedLetters.ifEmpty { "…" },
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.primary
            )
            EraseButton(onEraseClick = onEraseClick)
        }
    }
}

@Composable
private fun EraseButton(onEraseClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp))
            .clickable { onEraseClick() },
        contentAlignment = Alignment.Center
    ) {
        Text("⌫", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun LetterGrid(grid: List<Cell>, onCellClick: (Int) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        for (row in 0 until GRID_SIDE) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                for (column in 0 until GRID_SIDE) {
                    val index = row * GRID_SIDE + column
                    LetterCell(
                        cell = grid[index],
                        onClick = { onCellClick(index) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun LetterCell(cell: Cell, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier.aspectRatio(1f),
        enabled = !cell.isSelected,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Text(
            text = cell.char.toString(),
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun ActionButtons(onValidateClick: () -> Unit, onPassClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Button(
            onClick = onValidateClick,
            modifier = Modifier.weight(1f).height(52.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Valider", fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
        OutlinedButton(
            onClick = onPassClick,
            modifier = Modifier.weight(1f).height(52.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Passer", fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)
        }
    }
}

@Composable
private fun GameOverContent(
    score: Int,
    bestScore: Int,
    onReplayClick: () -> Unit,
    onHomeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "🏁", fontSize = 36.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Partie terminée",
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )
        FinalScoreCard(score = score)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Meilleur score de la session : $bestScore",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onReplayClick,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Rejouer", fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedButton(
            onClick = onHomeClick,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Retour à l'accueil", fontSize = 15.sp, color = MaterialTheme.colorScheme.onBackground)
        }
    }
}

@Composable
private fun FinalScoreCard(score: Int) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("MOTS TROUVÉS", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(
                text = score.toString(),
                fontSize = 40.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
