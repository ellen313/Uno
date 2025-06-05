package de.htwg.se.uno.controller.controllerComponent.command

import de.htwg.se.uno.controller.controllerComponent.base.GameBoard
import de.htwg.se.uno.controller.controllerComponent.base.command.ColorWishCommand
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.se.uno.model.*
import de.htwg.se.uno.model.gameComponent.base.GameState

class ColorWishCommandSpec extends AnyWordSpec with Matchers {

  "ColorWishCommand" should {

    "set the selected color in GameBoard on execute" in {
      val initialState = GameState(
        players = List(),
        currentPlayerIndex = 0,
        allCards = List(),
        isReversed = false,
        discardPile = List(),
        drawPile = List(),
        selectedColor = None
      )

      GameBoard.updateState(initialState)

      val command = ColorWishCommand("red")
      command.execute()

      GameBoard.gameState.get.selectedColor shouldBe Some("red")
    }

    "restore the previous state on undo" in {
      val initialState = GameState(
        players = List(),
        currentPlayerIndex = 0,
        allCards = List(),
        isReversed = false,
        discardPile = List(),
        drawPile = List(),
        selectedColor = Some("blue")
      )

      GameBoard.updateState(initialState)

      val command = ColorWishCommand("yellow")
      command.execute()

      GameBoard.gameState.get.selectedColor shouldBe Some("yellow")

      command.undo()
      GameBoard.gameState.get.selectedColor shouldBe Some("blue")
    }

    "reapply the color change on redo" in {
      val initialState = GameState(
        players = List(),
        currentPlayerIndex = 0,
        allCards = List(),
        isReversed = false,
        discardPile = List(),
        drawPile = List(),
        selectedColor = Some("green")
      )

      GameBoard.updateState(initialState)

      val command = ColorWishCommand("blue")
      command.execute()
      command.undo()
      command.redo()

      GameBoard.gameState.get.selectedColor shouldBe Some("blue")
    }
  }
}
