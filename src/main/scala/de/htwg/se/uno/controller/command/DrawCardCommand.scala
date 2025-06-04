package de.htwg.se.uno.controller.command

import de.htwg.se.uno.model.*
import de.htwg.se.uno.controller.GameBoard
import de.htwg.se.uno.util.Command

case class DrawCardCommand() extends Command {
  var drawnCard: Option[Card] = None
  private var previousState: Option[GameState] = None
  
  override def execute(): Unit = {
    GameBoard.gameState.foreach { state =>
      previousState = Some(state)
      val currentPlayer = state.players(state.currentPlayerIndex)
      val (cardDrawn, updatedPlayerHand, updatedDrawPile, updatedDiscardPile) =
        state.drawCard(
          playerHand = currentPlayer,
          drawPile = state.drawPile,
          discardPile = state.discardPile
        )

      drawnCard = Some(cardDrawn)

      val updatedPlayers = state.players.updated(
        state.currentPlayerIndex, updatedPlayerHand
      )

      val newGameState = state.copy(
        players = updatedPlayers,
        drawPile = updatedDrawPile,
        discardPile = updatedDiscardPile
      )

      GameBoard.updateState(newGameState)
    }
  }  

  override def undo(): Unit = {
    previousState.foreach { oldState =>
      GameBoard.updateState(oldState)
      oldState.notifyObservers()
    }
  }

  override def redo(): Unit = execute()
}
