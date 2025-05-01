
import controller.GameBoard

import scala.*
import model.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec
//import org.mockito.Mockito._
//import org.mockito.ArgumentMatchers._
import model._
import view._


class UnoTuiSpec extends AnyWordSpec {

  "UnoTui" should {

    "initialize correctly" in {
      val gameState = GameState(
        players = List(
          PlayerHand(List(NumberCard("red", 1), NumberCard("blue", 2))),
          PlayerHand(List(NumberCard("green", 3)))
        ),
        currentPlayerIndex = 0,
        drawPile = List(NumberCard("yellow", 4)),
        discardPile = List(NumberCard("red", 5)),
        isReversed = false,
        allCards = List()
      )

      val unoTui = new UnoTui(gameState)

      assert(unoTui.game eq gameState)
      assert(!unoTui.gameShouldExit)
      assert(unoTui.selectedColor.isEmpty)
    }

    "display should return early if game should exit" in {
      val gameState = GameState(
        players = List(
          PlayerHand(List(NumberCard("red", 1), NumberCard("blue", 2))),
          PlayerHand(List(NumberCard("green", 3)))
        ),
        currentPlayerIndex = 0,
        drawPile = List(NumberCard("yellow", 4)),
        discardPile = List(NumberCard("red", 5)),
        isReversed = false,
        allCards = List()
      )

      val unoTui = new UnoTui(gameState)
      unoTui.gameShouldExit = true

      // Simulate displaying without throwing exceptions
      val stream = new java.io.ByteArrayOutputStream()
      Console.withOut(stream) {
        unoTui.display()
      }
      assert(stream.toString.isEmpty) // No output expected
    }

    "display should return early if players are empty" in {
      val gameState = new GameState(
        players = List.empty,
        currentPlayerIndex = 0,
        drawPile = List(NumberCard("yellow", 4)),
        discardPile = List(NumberCard("red", 5)),
        isReversed = false,
        allCards = List()
      )

      val unoTui = new UnoTui(gameState)

      val stream = new java.io.ByteArrayOutputStream()
      Console.withOut(stream) {
        unoTui.display()
      }
      assert(stream.toString.isEmpty)
    }

    "display should show current player's turn and top card" in {
      val gameState = GameState(
        players = List(
          PlayerHand(List(NumberCard("red", 1), NumberCard("blue", 2))),
          PlayerHand(List(NumberCard("green", 3)))
        ),
        currentPlayerIndex = 0,
        drawPile = List(NumberCard("yellow", 4)),
        discardPile = List(NumberCard("red", 5)),
        isReversed = false,
        allCards = List()
      )

      val unoTui = new UnoTui(gameState)

      val stream = new java.io.ByteArrayOutputStream()
      Console.withOut(stream) {
        unoTui.display()
      }
      val output = stream.toString
      assert(output.contains("Player 1's turn!"))
      assert(output.contains("Top Card:"))
    }

    "display should show players who said UNO" in {
      val playerWithUno = PlayerHand(List(NumberCard("red", 1)))
      playerWithUno.hasSaidUno = true
      val gameState = GameState(
        players = List(playerWithUno, PlayerHand(List.empty)),
        currentPlayerIndex = 0,
        drawPile = List(NumberCard("yellow", 4)),
        discardPile = List(NumberCard("red", 5)),
        isReversed = false,
        allCards = List()
      )

      val unoTui = new UnoTui(gameState)

      val stream = new java.io.ByteArrayOutputStream()
      Console.withOut(stream) {
        unoTui.display()
      }
      val output = stream.toString
      assert(output.contains("Player 1 said UNO"))
    }

    "display should show selected color when defined" in {
      val gameState = GameState(
        players = List(
          PlayerHand(List(NumberCard("red", 1), NumberCard("blue", 2))),
          PlayerHand(List(NumberCard("green", 3)))
        ),
        currentPlayerIndex = 0,
        drawPile = List(NumberCard("yellow", 4)),
        discardPile = List(NumberCard("red", 5)),
        isReversed = false,
        allCards = List()
      )

      val unoTui = new UnoTui(gameState)
      unoTui.selectedColor = Some("blue")

      val stream = new java.io.ByteArrayOutputStream()
      Console.withOut(stream) {
        unoTui.display()
      }
      val output = stream.toString
      assert(output.contains("The color that was chosen: blue"))
    }

    "display should show hand of current player" in {
      val gameState = GameState(
        players = List(
          PlayerHand(List(NumberCard("red", 1), NumberCard("blue", 2))),
          PlayerHand(List(NumberCard("green", 3)))
        ),
        currentPlayerIndex = 0,
        drawPile = List(NumberCard("yellow", 4)),
        discardPile = List(NumberCard("red", 5)),
        isReversed = false,
        allCards = List()
      )

      val unoTui = new UnoTui(gameState)

      val stream = new java.io.ByteArrayOutputStream()
      Console.withOut(stream) {
        unoTui.display()
      }
      val output = stream.toString
      assert(output.contains("Your Cards:"))
      assert(output.contains("0 - "))
      assert(output.contains("1 - "))
    }

    "display should handle case when no playable cards exist" in {
      val gameState = GameState(
        players = List(
          PlayerHand(List(NumberCard("blue", 1), NumberCard("green", 2))),
          PlayerHand(List(NumberCard("green", 3)))
        ),
        currentPlayerIndex = 0,
        drawPile = List(NumberCard("yellow", 4)),
        discardPile = List(NumberCard("red", 5)),
        isReversed = false,
        allCards = List()
      )

      val unoTui = new UnoTui(gameState)

      val stream = new java.io.ByteArrayOutputStream()
      Console.withOut(stream) {
        unoTui.display()
      }
      val output = stream.toString

      assert(output.contains("No playable Card! You have to draw a Card..."))
    }


    "chooseWildColor should prompt for color selection" in {
      val gameState = GameState(
        players = List(
          PlayerHand(List(NumberCard("red", 1), NumberCard("blue", 2))),
          PlayerHand(List(NumberCard("green", 3)))
        ),
        currentPlayerIndex = 0,
        drawPile = List(NumberCard("yellow", 4)),
        discardPile = List(NumberCard("red", 5)),
        isReversed = false,
        allCards = List()
      )

      val unoTui = new UnoTui(gameState)

      val mockInput = () => "1" // "green"
      val stream = new java.io.ByteArrayOutputStream()
      Console.withOut(stream) {
        unoTui.chooseWildColor(mockInput)
      }
      val output = stream.toString
      assert(output.contains("Please choose a color for the Wild Card:"))
      assert(output.contains("0 - red"))
      assert(output.contains("1 - green"))
      assert(output.contains("2 - blue"))
      assert(output.contains("3 - yellow"))
      assert(unoTui.selectedColor.contains("green"))
    }

    "chooseWildColor should handle invalid input and retry" in {
      val gameState = GameState(
        players = List(
          PlayerHand(List(NumberCard("red", 1), NumberCard("blue", 2))),
          PlayerHand(List(NumberCard("green", 3)))
        ),
        currentPlayerIndex = 0,
        drawPile = List(NumberCard("yellow", 4)),
        discardPile = List(NumberCard("red", 5)),
        isReversed = false,
        allCards = List()
      )

      val unoTui = new UnoTui(gameState)

      val mockInput = () => "invalid" // Invalid input first
      val stream = new java.io.ByteArrayOutputStream()
      Console.withOut(stream) {
        unoTui.chooseWildColor(mockInput)
      }
      assert(unoTui.selectedColor.isDefined)
    }

    "handleCardSelection should handle draw command" in {
      val gameState = GameState(
        players = List(
          PlayerHand(List(NumberCard("red", 1), NumberCard("blue", 2))),
          PlayerHand(List(NumberCard("green", 3)))
        ),
        currentPlayerIndex = 0,
        drawPile = List(NumberCard("yellow", 4)),
        discardPile = List(NumberCard("red", 5)),
        isReversed = false,
        allCards = List()
      )

      val unoTui = new UnoTui(gameState)

      val updatedGame = GameState(
        players = List(
          PlayerHand(List(NumberCard("yellow", 4))),
          PlayerHand(List(NumberCard("green", 3)))
        ),
        currentPlayerIndex = 1,
        drawPile = List(),
        discardPile = List(NumberCard("red", 5)),
        isReversed = false,
        allCards = List()
      )

      unoTui.handleCardSelection("draw")
      assert(unoTui.game eq updatedGame)
    }

    "handleCardSelection should handle valid card selection" in {
      val gameState = GameState(
        players = List(
          PlayerHand(List(NumberCard("red", 1), NumberCard("blue", 2))),
          PlayerHand(List(NumberCard("green", 3)))
        ),
        currentPlayerIndex = 0,
        drawPile = List(NumberCard("yellow", 4)),
        discardPile = List(NumberCard("red", 5)),
        isReversed = false,
        allCards = List()
      )

      val unoTui = new UnoTui(gameState)

      val updatedGame = GameState(
        players = List(
          PlayerHand(List(NumberCard("red", 1))),
          PlayerHand(List(NumberCard("green", 3)))
        ),
        currentPlayerIndex = 1,
        drawPile = List(),
        discardPile = List(NumberCard("red", 5)),
        isReversed = false,
        allCards = List()
      )

      unoTui.handleCardSelection("0")
      assert(unoTui.game eq updatedGame)
    }

    "handleCardSelection should reject invalid card selection" in {
      val gameState = GameState(
        players = List(
          PlayerHand(List(NumberCard("red", 1), NumberCard("blue", 2))),
          PlayerHand(List(NumberCard("green", 3)))
        ),
        currentPlayerIndex = 0,
        drawPile = List(NumberCard("yellow", 4)),
        discardPile = List(NumberCard("red", 5)),
        isReversed = false,
        allCards = List()
      )

      val unoTui = new UnoTui(gameState)

      val stream = new java.io.ByteArrayOutputStream()
      Console.withOut(stream) {
        unoTui.handleCardSelection("999")
      }
      val output = stream.toString
      assert(output.contains("Invalid index! Please select a valid card."))
    }
  }
}
