package de.htwg.se.uno.model.state

import de.htwg.se.uno.model.{GameState, PlayerHand, NumberCard}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class CheckWinnerStateSpec extends AnyWordSpec with Matchers {
  
  class GameStateWithWinner(players: List[PlayerHand]) extends GameState(
    players = players,
    currentPlayerIndex = 0,
    allCards = List(),
    isReversed = false,
    discardPile = List(),
    drawPile = List()
  ) {
    override def checkForWinner(): Option[Int] = Some(0)
  }
  
  class GameStateWithoutWinner(players: List[PlayerHand]) extends GameState(
    players = players,
    currentPlayerIndex = 0,
    allCards = List(),
    isReversed = false,
    discardPile = List(),
    drawPile = List()
  ) {
    override def checkForWinner(): Option[Int] = None
  }

  "CheckWinnerState" should {

    "transition to GameOverState if there is a winner" in {
      val players = List(
        PlayerHand(List(NumberCard("red", 5)))
      )
      val gameState = new GameStateWithWinner(players)
      val unoStates = new UnoStates(gameState)
      val checkWinnerState = CheckWinnerState(unoStates)

      unoStates.setState(checkWinnerState)
      val newState = unoStates.state.checkForWinner()

      newState shouldBe a [GameOverState]
    }

    "transition to PlayerTurnState if there is no winner" in {
      val players = List(
        PlayerHand(List(NumberCard("red", 5)))
      )
      val gameState = new GameStateWithoutWinner(players)
      val unoStates = new UnoStates(gameState)
      val checkWinnerState = CheckWinnerState(unoStates)

      unoStates.setState(checkWinnerState)
      val newState = unoStates.state.checkForWinner()

      newState shouldBe a [PlayerTurnState]
    }
  }
}
