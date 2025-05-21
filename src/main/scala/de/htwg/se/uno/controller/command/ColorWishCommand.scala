package de.htwg.se.uno.controller.command

import de.htwg.se.uno.model.*
import de.htwg.se.uno.model.state.UnoPhases
import de.htwg.se.uno.controller.GameBoard
import de.htwg.se.uno.util.Command

case class ColorWishCommand(color: String) extends Command {

  //for undo operation
  private var previousState: Option[GameState] = None

  override def execute(): Unit = {
    GameBoard.gameState.foreach { state =>
      previousState = Some(state)

      val updatedState: GameState = state.setSelectedColor(color)

      GameBoard.updateState(updatedState)

      println(s"Chose color for Wild Card: $color")
    }
  }

  override def undo(): Unit = {
    previousState.foreach { oldState =>
      GameBoard.updateState(oldState)
      println(s"Undo: color reseted")
    }
  }

  override def redo(): Unit = execute()
}
