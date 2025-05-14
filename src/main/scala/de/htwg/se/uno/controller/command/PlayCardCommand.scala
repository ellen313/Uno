package de.htwg.se.uno.controller.command

import de.htwg.se.uno.model.*
import de.htwg.se.uno.controller.GameBoard

case class PlayCardCommand(var gameBoard: GameBoard, card: Card) extends Command {
  override def execute(): Unit = {
    val state = gameBoard.gameState
    val playerIndex = state.currentPlayerIndex
    val currentPlayer = state.players(playerIndex)

    if (state.isValidPlay(card, state.discardPile.lastOption, state.selectedColor)) {

      val updatedHand = currentPlayer.removeCard(card)

      val updatedPlayers = state.players.updated(playerIndex, updatedHand)

      val newDiscardPile = state.discardPile :+ card

      var updatedGameState = state.copy(
        players = updatedPlayers,
        discardPile = newDiscardPile,
        selectedColor = if (card.isInstanceOf[WildCard]) state.selectedColor else None // WildColor nur bei Wild behalten
      )

     card match {
        case ActionCard(_, "skip") =>
          updatedGameState = updatedGameState.nextPlayer().nextPlayer()

        case ActionCard(_, "reverse") =>
          updatedGameState = updatedGameState.copy(isReversed = !updatedGameState.isReversed).nextPlayer()

        case ActionCard(_, "draw two") =>
          updatedGameState = updatedGameState.handleDrawCards(2).nextPlayer()

        case WildCard("wild draw four") =>
          updatedGameState = updatedGameState.handleDrawCards(4).nextPlayer()

        case _ =>
          updatedGameState = updatedGameState.nextPlayer()
      }

      val finalHand =
        if (updatedHand.hasUno) updatedHand.sayUno()
        else updatedHand.resetUnoStatus()

      val finalPlayers = updatedGameState.players.updated(playerIndex, finalHand)
      updatedGameState = updatedGameState.copy(players = finalPlayers)

      gameBoard.gameState = updatedGameState
      updatedGameState.notifyObservers()

    } else {
      println("Invalid play")
    }
  }
}
