package de.htwg.se.uno.controller.command

import de.htwg.se.uno.model.*
import de.htwg.se.uno.model.state.UnoStates
import de.htwg.se.uno.controller.GameBoard

case class ColorWishCommand(gameBoard: GameBoard, color: String) extends Command {
  override def execute(): Unit = {
    // Hole den aktuellen Spielzustand (UnoStates)
    val gameState = gameBoard.gameState

    // Setze die ausgewählte Farbe im aktuellen Zustand
    gameState.setSelectedColor(color)

    // Aktualisiere die Beobachter
    gameBoard.gameState.notifyObservers()

    println(s"Farbe für Wild Card gesetzt: $color")
  }
}
