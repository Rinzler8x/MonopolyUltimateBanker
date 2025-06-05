package com.example.monopolyultimatebanker.data.gametable

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GameRepositoryImpl @Inject constructor(
    private val gameDao: GameDao
) : GameRepository {
    override fun getGameStream(): Flow<List<Game>> = gameDao.getGame()

    override fun getGamePlayer(playerId: String): Game = gameDao.getPlayer(playerId)

    override fun gamePlayerExists(playerName: String): PlayerState = gameDao.playerExists(playerName)

    override suspend fun updatePlayerState(amount: Int, playerName: String) = gameDao.updatePlayerState(amount, playerName)

    override suspend fun gameInsert(game: Game) = gameDao.insert(game)

    override suspend fun deletePlayer(playerBalance: Int) = gameDao.deletePlayer(playerBalance)

    override suspend fun deleteGame() = gameDao.deleteGame()
}