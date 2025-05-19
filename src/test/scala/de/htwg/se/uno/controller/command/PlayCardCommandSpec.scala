package de.htwg.se.uno.controller.command

import de.htwg.se.uno.model._
import de.htwg.se.uno.controller.GameBoard
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers._
import java.io.{ByteArrayInputStream, ByteArrayOutputStream, PrintStream}

class PlayCardCommandSpec extends AnyWordSpec {

  "A PlayCardCommand" should {

    val player1 = PlayerHand(List(NumberCard("red", 5), WildCard("wild")))
    val player2 = PlayerHand(List(NumberCard("green", 7)))
    val baseState = GameState(
      players = List(player1, player2),
      currentPlayerIndex = 0,
      allCards = Nil,
      isReversed = false,
      drawPile = List(NumberCard("yellow", 1), NumberCard("green", 4)),
      discardPile = List(NumberCard("red", 3)),
      selectedColor = None
    )

    "play a valid number card and update state correctly" in {
      GameBoard.initGame(baseState)

      val command = PlayCardCommand(NumberCard("red", 5))
      command.execute()

      val updated = GameBoard.gameState
      updated.players(0).cards should not contain NumberCard("red", 5)
      updated.discardPile.head shouldBe NumberCard("red", 5)
      updated.currentPlayerIndex shouldBe 1
    }

    "not allow an invalid card to be played" in {
      val invalidState = baseState.copy(
        players = List(PlayerHand(List(NumberCard("blue", 9))), player2),
        discardPile = List(NumberCard("red", 3))
      )
      GameBoard.initGame(invalidState)

      val out = new ByteArrayOutputStream()
      Console.withOut(new PrintStream(out)) {
        PlayCardCommand(NumberCard("blue", 9)).execute()
      }

      val updated = GameBoard.gameState
      out.toString should include("Invalid play")
      updated.players(0).cards should contain(NumberCard("blue", 9))
      updated.discardPile.head shouldBe NumberCard("red", 3)
    }

    "handle WildCard with user color input" in {
      val wildState = baseState.copy(
        players = List(PlayerHand(List(WildCard("wild"))), player2),
        discardPile = List(NumberCard("green", 4))
      )
      GameBoard.initGame(wildState)

      val input = new ByteArrayInputStream("2\n".getBytes) // choose "blue"
      Console.withIn(input) {
        PlayCardCommand(WildCard("wild")).execute()
      }

      val updated = GameBoard.gameState
      updated.discardPile.head shouldBe WildCard("wild")
      updated.selectedColor shouldBe Some("blue")
      updated.players(0).cards.exists(_.isInstanceOf[WildCard]) shouldBe false
    }

    "handle draw two and skip next player" in {
      val state = baseState.copy(
        players = List(PlayerHand(List(ActionCard("red", "draw two"))), player2),
        discardPile = List(NumberCard("red", 1)),
        drawPile = List(NumberCard("yellow", 3), NumberCard("green", 2), NumberCard("blue", 5))
      )
      GameBoard.initGame(state)

      PlayCardCommand(ActionCard("red", "draw two")).execute()

      val updated = GameBoard.gameState
      updated.players(1).cards.size shouldBe 3 // had 1, drew 2
      updated.currentPlayerIndex shouldBe 0    // skipped
    }

    "handle reverse correctly" in {
      val state = baseState.copy(
        players = List(PlayerHand(List(ActionCard("red", "reverse"))), player2),
        discardPile = List(NumberCard("red", 1)),
        isReversed = false
      )
      GameBoard.initGame(state)

      PlayCardCommand(ActionCard("red", "reverse")).execute()

      val updated = GameBoard.gameState
      updated.isReversed shouldBe true
      updated.currentPlayerIndex shouldBe 1
    }

    "handle wild draw four with color selection and drawing" in {
      val state = baseState.copy(
        players = List(PlayerHand(List(WildCard("wild draw four"))), player2),
        discardPile = List(NumberCard("yellow", 5)),
        drawPile = List(NumberCard("blue", 3), NumberCard("green", 8), NumberCard("yellow", 1), NumberCard("red", 9))
      )
      GameBoard.initGame(state)

      val input = new ByteArrayInputStream("1\n".getBytes) // choose "green"
      Console.withIn(input) {
        PlayCardCommand(WildCard("wild draw four")).execute()
      }

      val updated = GameBoard.gameState
      updated.selectedColor shouldBe Some("green")
      updated.players(1).cards.size shouldBe 5 // had 1, drew 4
      updated.currentPlayerIndex shouldBe 0    // skipped
    }
  }
}
