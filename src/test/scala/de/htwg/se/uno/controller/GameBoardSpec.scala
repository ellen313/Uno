package de.htwg.se.uno.controller

import de.htwg.se.uno.model.*
import de.htwg.se.uno.controller.command.*
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*

class GameBoardSpec extends AnyWordSpec {

  "A GameBoard" should {
    val player1 = PlayerHand(List(NumberCard("red", 1), NumberCard("green", 5)))
    val player2 = PlayerHand(List(ActionCard("blue", "skip")))
    val initialState = GameState(
      players = List(player1, player2),
      currentPlayerIndex = 0,
      allCards = List.empty,
      isReversed = false,
      drawPile = List.empty,
      discardPile = List(NumberCard("red", 5))
    )
    
    GameBoard.initGame(initialState)

    "return the correct gameState when initialized" in {
      GameBoard.gameState.players should have size 2
      GameBoard.gameState.currentPlayerIndex shouldBe 0
    }

    "correctly update the state" in {
      val newState = GameBoard.gameState.copy(currentPlayerIndex = 1)
      GameBoard.updateState(newState)
      GameBoard.gameState.currentPlayerIndex shouldBe 1
    }

    "check for a valid play based on color" in {
      val validCard = NumberCard("red", 9)
      GameBoard.isValidPlay(validCard, NumberCard("red", 5), None) shouldBe true
    }

    "check for a valid play based on number" in {
      val validCard = NumberCard("green", 5)
      GameBoard.isValidPlay(validCard, NumberCard("red", 5), None) shouldBe true
    }

    "return false for invalid play" in {
      val invalidCard = NumberCard("blue", 2)
      GameBoard.isValidPlay(invalidCard, NumberCard("red", 5), None) shouldBe false
    }

    "correctly identify a winning player with no cards" in {
      val winState = GameBoard.gameState.copy(players = List(PlayerHand(Nil), player2))
      GameBoard.updateState(winState)
      GameBoard.checkForWinner() shouldBe Some(0)
    }

    "return None when no player has won" in {
      val noWinState = GameBoard.gameState.copy(players = List(player1, player2))
      GameBoard.updateState(noWinState)
      GameBoard.checkForWinner() shouldBe None
    }

    "execute a command and notify observers" in {
      var executed = false
      val dummyCommand = new Command {
        override def execute(): Unit = executed = true
      }

      GameBoard.executeCommand(dummyCommand)
      executed shouldBe true
    }

    "reset the game state correctly" in {
      GameBoard.reset()
      assertThrows[IllegalStateException] {
        GameBoard.gameState
      }
    }

    "shuffle the deck and return draw and discard piles" in {
      val (discard, draw) = GameBoard.shuffleDeck()
      discard should not be empty
      draw should not be empty
    }

    "initialize with shuffled deck and discard pile" in {
      val freshState = GameState(
        players = List(player1, player2),
        currentPlayerIndex = 0,
        allCards = List.empty,
        isReversed = false,
        drawPile = List.empty,
        discardPile = List.empty
      )
      
      GameBoard.initGame(freshState)
      GameBoard.gameState.discardPile should not be empty
    }
  }
}
