import scala._
import model._
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, PrintStream}

class UnoTui_neuSpec extends AnyWordSpec {

  "UnoTui" should {

    "display the current game state correctly" in {
      val topCard = NumberCard("red", 5)
      val playerHand = PlayerHand(List(NumberCard("red", 5), ActionCard("blue", "skip")))
      val gameBoard = GameBoard(
        drawPile = List.fill(5)(NumberCard("yellow", 3)),
        discardPile = List(topCard)
      )
      val gameState = GameState(List(playerHand), gameBoard, 0, List())
      val tui = new UnoTui_neu(gameState)

      val outputCapture = new ByteArrayOutputStream()
      Console.withOut(new PrintStream(outputCapture)) {
        tui.display()
      }

      val output = outputCapture.toString.trim
      output should include("Player 1's turn")
      output should include("NumberCard(red, 5)")
      output should include("NumberCard(red, 5)")
      output should include("ActionCard(blue, skip)")
    }

    "draw a card when input is 'draw'" in {
      val initialHand = PlayerHand(List(NumberCard("red", 5)))
      val topCard = NumberCard("green", 7)
      val drawPile = List(NumberCard("green", 6))
      
      val gameBoard = GameBoard(
        drawPile = drawPile,
        discardPile = List(topCard)
      )
      val gameState = GameState(List(initialHand), gameBoard, 0, drawPile)
      val tui = new UnoTui_neu(gameState)
      
      val output = new ByteArrayOutputStream()

      Console.withOut(new PrintStream(output)) {
        Console.withIn(new ByteArrayInputStream("draw\n".getBytes)) {
          tui.handleCardSelection("draw")
        }
      }

      val updateHand = gameState.players.head.cards
      updateHand should contain(NumberCard("red", 5))
    }

    "reject invalid card index" in {
      val hand = PlayerHand(List(NumberCard("red", 5)))
      val gameBoard = GameBoard(
        drawPile = List(NumberCard("yellow", 3)),
        discardPile = List(NumberCard("red", 4))
      )
      val gameState = GameState(List(hand), gameBoard, 0, List())
      val tui = new UnoTui_neu(gameState)

      val input = new ByteArrayInputStream("10\n".getBytes())
      val output = new ByteArrayOutputStream()

      Console.withIn(input) {
        Console.withOut(new PrintStream(output)) {
          tui.handleCardSelection("10")
        }
      }
    }

    "display players who said UNO" in {
      val player1 = PlayerHand(List(NumberCard("red", 1)), hasSaidUno = true)
      val player2 = PlayerHand(List(NumberCard("blue", 2)))
      val gameBoard = GameBoard(
        drawPile = List.fill(5)(NumberCard("yellow", 3)),
        discardPile = List(NumberCard("red", 3)))
      val gameState = GameState(List(player1, player2), gameBoard, 0, List())
      val tui = new UnoTui_neu(gameState)

      val outputCapture = new ByteArrayOutputStream()
      Console.withOut(new PrintStream(outputCapture)) {
        tui.display()
      }

      val output = outputCapture.toString
      output should include("Player 1 said UNO")
    }

    "display selected color when defined" in {
      val player = PlayerHand(List(NumberCard("red", 1)))
      val gameBoard = GameBoard(
        drawPile = List.fill(5)(NumberCard("yellow", 3)),
        discardPile = List(NumberCard("red", 3)))
      val gameState = GameState(List(player), gameBoard, 0, List())
      val tui = new UnoTui_neu(gameState)
      tui.selectedColor = Some("blue")

      val outputCapture = new ByteArrayOutputStream()
      Console.withOut(new PrintStream(outputCapture)) {
        tui.display()
      }

      val output = outputCapture.toString
      output should include("The color that was chosen: blue")
    }

    "handle wild card color selection" in {
      val player = PlayerHand(List(WildCard("wild")))
      val gameBoard = GameBoard(
        drawPile = List.empty,
        discardPile = List(NumberCard("green", 3)))
      val gameState = GameState(List(player), gameBoard, 0, List())
      val tui = new UnoTui_neu(gameState)

      // Simulate user input "1" (for green)
      val input = new ByteArrayInputStream("1\n".getBytes)
      val output = new ByteArrayOutputStream()

      Console.withIn(input) {
        Console.withOut(new PrintStream(output)) {
          tui.chooseWildColor()
        }
      }

      tui.selectedColor shouldBe Some("green")
      output.toString should include("Wild Card color changed to: green")
    }

    "handle invalid color selection" in {
      val player = PlayerHand(List(WildCard("wild")))
      val gameBoard = GameBoard(
        drawPile = List.empty,
        discardPile = List(NumberCard("green", 3)))
      val gameState = GameState(List(player), gameBoard, 0, List())
      val tui = new UnoTui_neu(gameState)

      // Simulate invalid input ("abc") then valid input ("2")
      val input = new ByteArrayInputStream("abc\n2\n".getBytes)
      val output = new ByteArrayOutputStream()

      Console.withIn(input) {
        Console.withOut(new PrintStream(output)) {
          tui.chooseWildColor()
        }
      }

      val outputStr = output.toString
      outputStr should include("Invalid input. Please enter a number between 0 and 3.")
      tui.selectedColor shouldBe Some("blue")
    }

    "handle playing a wild card" in {
      val player = PlayerHand(List(WildCard("wild"), NumberCard("red", 1))) // Extra card
      val gameBoard = GameBoard(
        drawPile = List.fill(5)(NumberCard("yellow", 3)),
        discardPile = List(NumberCard("green", 3)))
      val gameState = GameState(List(player), gameBoard, 0, List())
      val tui = new UnoTui_neu(gameState)

      val input = new ByteArrayInputStream("1\n".getBytes) // Choose green
      val output = new ByteArrayOutputStream()

      Console.withIn(input) {
        Console.withOut(new PrintStream(output)) {
          tui.handleCardSelection("0") // Play wild card
        }
      }

      val outputStr = output.toString
      outputStr should include("Played: WildCard(wild)")
      outputStr should include("Wild Card color changed to: green")
      tui.selectedColor shouldBe Some("green")
      tui.shouldExit shouldBe false // Verify game continues
    }

    "handle invalid play when color doesn't match selected color" in {
      val player = PlayerHand(List(NumberCard("red", 1)))
      val gameBoard = GameBoard(
        drawPile = List.fill(5)(NumberCard("green", 3)),
        discardPile = List(NumberCard("green", 3)))
      val gameState = GameState(List(player), gameBoard, 0, List())
      val tui = new UnoTui_neu(gameState)
      tui.selectedColor = Some("blue")

      val output = new ByteArrayOutputStream()
      Console.withOut(new PrintStream(output)) {
        tui.handleCardSelection("0")
      }

      val outputStr = output.toString
      outputStr should include("Invalid play! The color must be blue")
    }

    "announce winner when a player has no cards left" in {
      val player = PlayerHand(List.empty)
      val gameBoard = GameBoard(
        drawPile = List.empty,
        discardPile = List(NumberCard("red", 3)))
      val gameState = GameState(List(player), gameBoard, 0, List())
      val tui = new UnoTui_neu(gameState)

      val output = new ByteArrayOutputStream()
      Console.withOut(new PrintStream(output)) {
        tui.checkForWinner()
      }

      val outputStr = output.toString
      outputStr should include("Player 1 wins! Game over.")
      tui.shouldExit shouldBe true
    }
    "handle out-of-range wild card color input" in {
      val player = PlayerHand(List(WildCard("wild")))
      val gameBoard = GameBoard(
        drawPile = List.empty,
        discardPile = List(NumberCard("green", 3)))
      val gameState = GameState(List(player), gameBoard, 0, List())
      val tui = new UnoTui_neu(gameState)

      val input = new ByteArrayInputStream("5\n2\n".getBytes)
      val output = new ByteArrayOutputStream()

      Console.withIn(input) {
        Console.withOut(new PrintStream(output)) {
          tui.chooseWildColor()
        }
      }

      val outputStr = output.toString
      outputStr should include("Invalid color choice. Please try again.")
      outputStr should include("Wild Card color changed to: blue")
      tui.selectedColor shouldBe Some("blue")
    }

    "reject invalid card play when the card does not match the top card" in {
      val player = PlayerHand(List(NumberCard("red", 1)))
      val gameBoard = GameBoard(
        drawPile = List.fill(5)(NumberCard("yellow", 3)),
        discardPile = List(NumberCard("green", 3))
      )
      val gameState = GameState(List(player), gameBoard, 0, List())
      val tui = new UnoTui_neu(gameState)

      val input = new ByteArrayInputStream("0\n".getBytes)
      val output = new ByteArrayOutputStream()

      Console.withIn(input) {
        Console.withOut(new PrintStream(output)) {
          tui.handleCardSelection("0")
        }
      }
      val outputStr = output.toString
      outputStr should include("Invalid card! Please select a valid card.")
    }

    "reject invalid input when a non-numeric value is entered for card selection" in {
      val player = PlayerHand(List(NumberCard("red", 1)))
      val gameBoard = GameBoard(
        drawPile = List.fill(5)(NumberCard("yellow", 3)),
        discardPile = List(NumberCard("green", 3))
      )
      val gameState = GameState(List(player), gameBoard, 0, List())
      val tui = new UnoTui_neu(gameState)

      val input = new ByteArrayInputStream("abc\n".getBytes)
      val output = new ByteArrayOutputStream()

      Console.withIn(input) {
        Console.withOut(new PrintStream(output)) {
          tui.handleCardSelection("abc")
        }
      }

      val outputStr = output.toString
      outputStr should include("Invalid input! Please select a valid index or type 'draw':")
    }

    "not say UNO when player has 2 cards but already said UNO" in {
      val player = PlayerHand(
        List(NumberCard("red", 1), NumberCard("blue", 2)),
        hasSaidUno = true
      )
      val gameBoard = GameBoard(
        drawPile = List.fill(5)(NumberCard("yellow", 3)),
        discardPile = List(NumberCard("red", 3))
      )
      val gameState = GameState(List(player), gameBoard, 0, List())
      val tui = new UnoTui_neu(gameState)

      val output = new ByteArrayOutputStream()
      Console.withOut(new PrintStream(output)) {
        tui.handleCardSelection("0")
      }

      val outputStr = output.toString
      outputStr should not include "You said 'UNO'!"
      gameState.players.head.hasSaidUno shouldBe true
    }
  }
}