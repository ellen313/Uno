package de.htwg.se.uno.aview

import de.htwg.se.uno.controller.controllerComponent.base.GameBoard
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.uno.model.*
import de.htwg.se.uno.model.cardComponent.{NumberCard, WildCard}
import de.htwg.se.uno.model.gameComponent.base.GameState
import de.htwg.se.uno.model.gameComponent.base.state.UnoPhases
import de.htwg.se.uno.model.playerComponent.PlayerHand

class UnoTUISpec extends AnyWordSpec with Matchers {

  "UnoTui" should {

    val playerHand = PlayerHand(List(NumberCard("red", 5), WildCard("wild")), hasSaidUno = false)

    val gameState = GameState(
      players = List(playerHand),
      currentPlayerIndex = 0,
      allCards = List(NumberCard("red", 5), WildCard("wild")),
      isReversed = false,
      drawPile = List(NumberCard("red", 9)),
      discardPile = List(NumberCard("red", 3)),
      selectedColor = None
    )

    GameBoard.updateState(gameState)
    val context = new UnoPhases(gameState)
    val tui = new UnoTUI(GameBoard)

    "display the game state without throwing" in {
      GameBoard.gameState match {
        case scala.util.Success(_) => noException should be thrownBy tui.display()
        case scala.util.Failure(e) => fail(s"GameState not initialized: ${e.getMessage}")
      }
    }

    "handle a valid card index input" in {
      GameBoard.gameState match {
        case scala.util.Success(_) => noException should be thrownBy tui.handleInput("0")
        case scala.util.Failure(e) => fail(s"GameState not initialized: ${e.getMessage}")
      }
    }

    "handle invalid (non-integer) input gracefully" in {
      noException should be thrownBy tui.handleInput("invalid")
    }

    "handle 'draw' command without throwing" in {
      noException should be thrownBy tui.handleInput("draw")
    }

    "choose wild color with simulated input" in {
      val simulatedInput = () => "2" // blue
      val chosen = tui.chooseWildColor(simulatedInput)
      chosen shouldBe "blue"
    }

    "detect win and set shouldExit to true" in {
      val winningPlayer = PlayerHand(Nil, hasSaidUno = true)
      val winningState = gameState.copy(players = List(winningPlayer))
      GameBoard.updateState(winningState)

      GameBoard.checkForWinner().isDefined shouldBe true
    }

    "trigger update without throwing" in {
      noException should be thrownBy tui.update()
    }
  }
}
