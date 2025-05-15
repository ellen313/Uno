package de.htwg.se.uno.controller.command

import de.htwg.se.uno.model.*
import de.htwg.se.uno.controller.GameBoard
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*

class PlayCardCommandSpec extends AnyWordSpec {

  "A PlayCardCommand" should {

    val player1 = PlayerHand(List(NumberCard("red", 5), WildCard("wild")))
    val player2 = PlayerHand(List(NumberCard("green", 7)))
    val initialState = GameState(
      players = List(player1, player2),
      currentPlayerIndex = 0,
      allCards = Nil,
      isReversed = false,
      drawPile = List(NumberCard("yellow", 1), NumberCard("green", 4)),
      discardPile = List(NumberCard("red", 3)),
      selectedColor = None
    )

    "play a valid number card and update the state" in {
      GameBoard.initGame(initialState)

      val command = PlayCardCommand(NumberCard("red", 5))
      command.execute()

      val updatedState = GameBoard.gameState

      updatedState.players(0).cards should not contain NumberCard("red", 5)
      updatedState.discardPile.head shouldBe NumberCard("red", 5)
      updatedState.currentPlayerIndex shouldBe 1
    }

    "prompt for color when a WildCard is played" in {
      GameBoard.initGame(initialState.copy(
        players = List(PlayerHand(List(WildCard("wild"))), player2),
        discardPile = List(NumberCard("green", 7)),
        selectedColor = None
      ))

      // Mock stdin for color input
      val input = new java.io.ByteArrayInputStream("2\n".getBytes) // Select "blue"
      Console.withIn(input) {
        val command = PlayCardCommand(WildCard("wild"))
        command.execute()
      }

      val updatedState = GameBoard.gameState

      updatedState.discardPile.head shouldBe WildCard("wild")
      updatedState.selectedColor shouldBe Some("blue")
      updatedState.players(0).cards.exists(_.isInstanceOf[WildCard]) shouldBe false
    }

    "not update state if the play is invalid" in {
      GameBoard.initGame(initialState.copy(
        players = List(PlayerHand(List(NumberCard("blue", 9))), player2),
        discardPile = List(NumberCard("red", 3)),
        selectedColor = None
      ))

      val command = PlayCardCommand(NumberCard("blue", 9))
      command.execute()

      val updatedState = GameBoard.gameState

      updatedState.players(0).cards should contain(NumberCard("blue", 9))
      updatedState.discardPile.head shouldBe NumberCard("red", 3)
      updatedState.currentPlayerIndex shouldBe 0 // No player switch
    }

    "handle draw two and skip appropriately" in {
      val state = initialState.copy(
        players = List(PlayerHand(List(ActionCard("red", "draw two"))), player2),
        discardPile = List(NumberCard("red", 9))
      )
      GameBoard.initGame(state)

      val command = PlayCardCommand(ActionCard("red", "draw two"))
      command.execute()

      val updatedState = GameBoard.gameState
      updatedState.currentPlayerIndex shouldBe 0 // Player 2 was skipped after drawing
      updatedState.players(1).cards.size shouldBe 3 // 1 original + 2 drawn
    }

    "handle reverse card correctly" in {
      val state = initialState.copy(
        players = List(PlayerHand(List(ActionCard("red", "reverse"))), player2),
        discardPile = List(NumberCard("red", 2)),
        isReversed = false
      )
      GameBoard.initGame(state)

      val command = PlayCardCommand(ActionCard("red", "reverse"))
      command.execute()

      val updatedState = GameBoard.gameState
      updatedState.isReversed shouldBe true
      updatedState.currentPlayerIndex shouldBe 1
    }
  }
}
