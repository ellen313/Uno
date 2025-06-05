package de.htwg.se.uno.controller.controllerComponent.command

import de.htwg.se.uno.controller.controllerComponent.base.GameBoard
import de.htwg.se.uno.controller.controllerComponent.base.command.UnoCalledCommand
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.se.uno.model.*
import de.htwg.se.uno.model.cardComponent.NumberCard
import de.htwg.se.uno.model.gameComponent.base.GameState
import de.htwg.se.uno.model.gameComponent.base.state.{GameOverPhase, UnoPhases}
import de.htwg.se.uno.model.playerComponent.PlayerHand

class UnoCalledCommandSpec extends AnyWordSpec with Matchers {

  "UnoCalledCommand" should {

    "set GameOverPhase when current player has no cards and has said UNO" in {
      val player = PlayerHand(cards = List(), hasSaidUno = true)
      val players = List(player)

      val gameState = GameState(
        players = players,
        currentPlayerIndex = 0,
        allCards = List(),
        isReversed = false,
        discardPile = List(),
        drawPile = List()
      )

      GameBoard.updateState(gameState)

      val unoStates = new UnoPhases(gameState)
      val command = UnoCalledCommand(Some(unoStates))

      command.execute()

      unoStates.state shouldBe a[GameOverPhase]
    }


    "update game state and switch to GameOverState if player has no cards and said UNO" in {
      val playerWithNoCards = PlayerHand(List(), hasSaidUno = true)
      val otherPlayer = PlayerHand(List(NumberCard("red", 5)))
      
      val initialState = GameState(
        players = List(playerWithNoCards, otherPlayer),
        currentPlayerIndex = 0,
        allCards = List(),
        isReversed = false,
        discardPile = List(),
        drawPile = List()
      )

      GameBoard.updateState(initialState)

      val unoStates = new UnoPhases(initialState)
      val command = UnoCalledCommand(Some(unoStates))

      command.execute()

      unoStates.state.getClass.getSimpleName shouldBe "GameOverPhase"
    }

    "update game state and not change state if player still has cards or hasn't said UNO" in {
      val playerWithCards = PlayerHand(List(NumberCard("red", 5)), hasSaidUno = false)
      val otherPlayer = PlayerHand(List(NumberCard("blue", 3)))

      val initialState = GameState(
        players = List(playerWithCards, otherPlayer),
        currentPlayerIndex = 0,
        allCards = List(),
        isReversed = false,
        discardPile = List(),
        drawPile = List()
      )

      GameBoard.updateState(initialState)

      val unoStates = new UnoPhases(initialState)
      val command = UnoCalledCommand(Some(unoStates))

      command.execute()
      
      unoStates.state.getClass.getSimpleName should not be "GameOverPhase"
    }
  }
}
