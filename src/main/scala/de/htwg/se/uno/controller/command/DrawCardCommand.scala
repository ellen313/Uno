package de.htwg.se.uno.controller.command

import de.htwg.se.uno.model.*
import de.htwg.se.uno.controller.GameBoard

case class DrawCardCommand(gameBoard: GameBoard) extends Command {
  var drawnCard: Option[Card] = None
  
  override def execute(): Unit = {
    val game = gameBoard.gameState
    val currentPlayer = game.players(game.currentPlayerIndex)
    val (drawnCard, updatedPlayerHand, updatedDrawPile, updatedDiscardPile) =
      game.drawCard(
        playerHand = currentPlayer,
        drawPile = game.drawPile,
        discardPile = game.discardPile
      )

    val updatedPlayers = game.players.updated(
      game.currentPlayerIndex, updatedPlayerHand
    )

    val newGameState = game.copy(
      players = updatedPlayers,
      drawPile = updatedDrawPile,
      discardPile = updatedDiscardPile
    )
    
    gameBoard.gameState = newGameState

    gameBoard.gameState.notifyObservers()
  }
}
