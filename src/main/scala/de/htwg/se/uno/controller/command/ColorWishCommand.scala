package de.htwg.se.uno.controller.command

import de.htwg.se.uno.model.*
import de.htwg.se.uno.model.state.UnoStates
import de.htwg.se.uno.controller.GameBoard

case class ColorWishCommand(color: String) extends Command {
  override def execute(): Unit = {
    val gameState = GameBoard.gameState

    gameState.setSelectedColor(color)

    GameBoard.gameState.notifyObservers()

    println(s"Farbe für Wild Card gesetzt: $color")
  }
}
