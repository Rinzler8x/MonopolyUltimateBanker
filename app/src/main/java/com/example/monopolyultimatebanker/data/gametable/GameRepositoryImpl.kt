package com.example.monopolyultimatebanker.data.gametable

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GameRepositoryImpl @Inject constructor(
    private val gameDao: GameDao
) : GameRepository {
    override fun getGameStream(): Flow<List<Game>> = gameDao.getGame()

    override fun getGamePlayer(playerId: String): Game = gameDao.getPlayer(playerId)

    override fun gamePlayerExists(playerName: String):Int = gameDao.playerExists(playerName)

    override suspend fun updatePlayerState(amount: Int, playerName: String) = gameDao.updatePlayerState(amount, playerName)

    override suspend fun gameUpdateTempPlayerId(playerId: String, playerName: String) = gameDao.updateTempPlayerId(playerId, playerName)

    override suspend fun gameDeductBalance(payerId: String, amount: Int) = gameDao.deductBalance(payerId, amount)

    override suspend fun gameAddBalance(recipientId: String, amount: Int) = gameDao.addBalance(recipientId, amount)

    override suspend fun gameTransferRent(payerId: String, recipientId: String, amount: Int) = gameDao.transferRent(payerId, recipientId, amount)

    override suspend fun gameCollect200BothPlayers(player1Id: String, player2Id: String) = gameDao.collect200BothPlayers(player1Id, player2Id)

    override suspend fun gameEventDeduct50PerProperty(playerId: String) = gameDao.eventDeduct50PerProperty(playerId)

    override suspend fun gameInsert(game: Game) = gameDao.insert(game)
}