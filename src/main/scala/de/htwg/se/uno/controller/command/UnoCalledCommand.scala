package de.htwg.se.uno.controller.command

import de.htwg.se.uno.controller.GameBoard
import de.htwg.se.uno.model.GameState
import de.htwg.se.uno.model.state.{GameOverPhase, UnoPhases}
import de.htwg.se.uno.util.Command

case class UnoCalledCommand(context: Option[UnoPhases] = None) extends Command {
  private var previousState: Option[GameState] = None

  override def execute(): Unit = {
    GameBoard.gameState.foreach { state =>
      previousState = Some(state)
      val idx = state.currentPlayerIndex
      val updatedGame = state.playerSaysUno(idx)
      GameBoard.updateState(updatedGame)

      val player = updatedGame.players(idx)
      if (player.cards.isEmpty && player.hasSaidUno) {
        context.foreach(state => state.setState(GameOverPhase(state)))
      }
    }
  }

  override def undo(): Unit = {
    previousState.foreach { oldState =>
      GameBoard.updateState(oldState)
    }
  }

  override def redo(): Unit = execute()
}
