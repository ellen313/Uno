package de.htwg.se.uno.controller.command

import de.htwg.se.uno.model.*
import de.htwg.se.uno.controller.GameBoard

case class DrawCardCommand() extends Command {
  var drawnCard: Option[Card] = None
  
  override def execute(): Unit = {
    val game = GameBoard.gameState
    val currentPlayer = game.players(game.currentPlayerIndex)
    val (cardDrawn, updatedPlayerHand, updatedDrawPile, updatedDiscardPile) =
      game.drawCard(
        playerHand = currentPlayer,
        drawPile = game.drawPile,
        discardPile = game.discardPile
      )
      
    drawnCard = Some(cardDrawn)  

    val updatedPlayers = game.players.updated(
      game.currentPlayerIndex, updatedPlayerHand
    )

    val newGameState = game.copy(
      players = updatedPlayers,
      drawPile = updatedDrawPile,
      discardPile = updatedDiscardPile
    )

    GameBoard.updateState(newGameState)

    GameBoard.gameState.notifyObservers()
  }
}
