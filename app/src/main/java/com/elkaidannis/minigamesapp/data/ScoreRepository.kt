package com.elkaidannis.minigamesapp.data

class ScoreRepository(private val dao: ScoreDao) {

    suspend fun insertScore(score: Score) = dao.insertScore(score)

    suspend fun getTopScores(): List<Score> = dao.getTopScores()

    suspend fun getTopScoresByGame(gameName: String): List<Score> = dao.getTopScoresByGame(gameName)

    suspend fun getPlayerGameCount(playerName: String): Int = dao.getPlayerGameCount(playerName)

    suspend fun getPlayerAverageScore(playerName: String): Float = dao.getPlayerAverageScore(playerName)

    suspend fun deleteAllScores() = dao.deleteAllScores()
}
