package de.htwg.se.uno.controller

import de.htwg.se.uno.model.*
import de.htwg.se.uno.controller.command.*
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*

class GameBoardSpec extends AnyWordSpec {

  "A GameBoard" should {

    val player1 = PlayerHand(List(NumberCard("red", 1), NumberCard("green", 5)))
    val player2 = PlayerHand(List(ActionCard("blue", "skip")))
    val gameState = GameState(
      players = List(player1, player2),
      currentPlayerIndex = 0,
      allCards = List.empty,
      isReversed = false,
      drawPile = List.empty,
      discardPile = List.empty
    )
    
    GameBoard.init_state(gameState)
    

    "return the correct players and currentPlayerIndex" in {
      GameBoard.players should have size 2
      GameBoard.currentPlayerIndex shouldBe 0
    }

    "allow setting and getting the selected color" in {
      GameBoard.selectedColor shouldBe None
      GameBoard.setSelectedColor("green")
      GameBoard.selectedColor shouldBe Some("green")
    }

    "correctly validate a valid play" in {
      val card = NumberCard("red", 3)
      val topCard = NumberCard("red", 5)
      GameBoard.isValidPlay(card, topCard, None) shouldBe true
    }

    "identify no winner if players have cards" in {
      GameBoard.checkForWinner() shouldBe None
    }

    "identify the winning player when they have no cards" in {
      val winningState = gameState.copy(players = List(PlayerHand(Nil), player2))
      val winningBoard = GameBoard.copy(gameState = winningState)
      winningBoard.checkForWinner() shouldBe Some(0)
    }

    "execute a command and notify observers" in {
      var wasExecuted = false
      val testCommand = new Command {
        override def execute(): Unit = wasExecuted = true
      }
      GameBoard.executeCommand(testCommand)
      wasExecuted shouldBe true
    }
  }

  class EmptyDeckGameBoard extends GameBoard(
    GameState(Nil, 0, Nil, isReversed = false, Nil, Nil),
    Nil, Nil
  ) {
    override def createDeckWithAllCards(): List[Card] = Nil
  }

  "A GameBoard with an empty deck" should {
    "return empty drawPile and discardPile when shuffled" in {
      val emptyGameBoard = new EmptyDeckGameBoard()
      val shuffled = emptyGameBoard.shuffleDeck()

      shuffled.drawPile shouldBe empty
      shuffled.discardPile shouldBe empty
    }
  }
}
