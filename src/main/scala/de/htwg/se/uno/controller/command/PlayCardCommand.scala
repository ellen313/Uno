package de.htwg.se.uno.controller.command

import de.htwg.se.uno.model.*
import de.htwg.se.uno.controller.GameBoard

case class PlayCardCommand(var gameBoard: GameBoard, card: Card) extends Command {
  override def execute(): Unit = {
    val playerIndex = gameBoard.gameState.currentPlayerIndex
    val currentPlayer = gameBoard.gameState.players(playerIndex)
    
    if (gameBoard.isValidPlay(card, gameBoard.discardPile.last, gameBoard.gameState.selectedColor)) {
      val updatedHand = currentPlayer.removeCard(card)
      val updatedPlayers = gameBoard.gameState.players.updated(playerIndex, updatedHand)
      val newDiscardPile = card :: gameBoard.discardPile

      gameBoard.gameState = gameBoard.gameState.copy(
        players = updatedPlayers,
        discardPile = newDiscardPile,
        selectedColor = None
      )
    } else {
      println("Invalid play")
    }
  }
}
