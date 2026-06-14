package com.elkaidannis.minigamesapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ScoreDao {

    @Insert
    suspend fun insertScore(score: Score)

    @Query("SELECT * FROM score ORDER BY score DESC LIMIT 10")
    suspend fun getTopScores(): List<Score>

    @Query("SELECT * FROM score WHERE game_name = :gameName ORDER BY score DESC LIMIT 10")
    suspend fun getTopScoresByGame(gameName: String): List<Score>

    @Query("SELECT COUNT(*) FROM score WHERE player_name = :playerName")
    suspend fun getPlayerGameCount(playerName: String): Int

    @Query("SELECT AVG(score) FROM score WHERE player_name = :playerName")
    suspend fun getPlayerAverageScore(playerName: String): Float

    @Query("DELETE FROM score")
    suspend fun deleteAllScores()
}
