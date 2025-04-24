import scala._
import model._
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, PrintStream}

class UnoTui_neuSpec extends AnyWordSpec {

  "UnoTui" should {

    "display the current game state correctly" in {
      val playerHand = PlayerHand(List(NumberCard("red", 5), ActionCard("blue", "skip")))
      val gameBoard = GameBoard(List(NumberCard("yellow", 3)), List())
      val gameState = GameState(List(playerHand), gameBoard, 0, List())
      val tui = new UnoTui_neu(gameState)

      val outputCapture = new ByteArrayOutputStream()
      Console.withOut(new PrintStream(outputCapture)) {
        tui.display()
      }

      val output = outputCapture.toString.trim
      output should include("Player 1's turn")
      output should include("Top Card")
      output should include("red-5")
      output should include("blue-skip")
    }

    "draw a card when input is 'draw'" in {
      val initialHand = PlayerHand(List(NumberCard("red", 5)))
      val drawPile = List(NumberCard("blue", 2))

      val card1 = NumberCard("red", 1)
      val card2 = ActionCard("green", "skip")
      val gameBoard = GameBoard(
        drawPile = List(),
        discardPile = List(card1, card2)
      )
      val gameState = GameState(List(initialHand), gameBoard, 0, drawPile)
      val tui = new UnoTui_neu(gameState)

      val input = new ByteArrayInputStream("draw\n".getBytes)
      val output = new ByteArrayOutputStream()

      Console.withIn(input) {
        Console.withOut(new PrintStream(output)) {
          tui.handleCardSelection("draw")
        }
      }

      val updateHand = gameState.players.head.cards
      updateHand.exists(_.color == "blue") shouldBe true
    }

    "reject invalid card index" in {
      val hand = PlayerHand(List(NumberCard("red", 5)))
      val gameBoard = GameBoard(List(NumberCard("yellow", 3)), List())
      val gameState = GameState(List(hand), gameBoard, 0, List())
      val tui = new UnoTui_neu(gameState)

      val input = new ByteArrayInputStream("10\n".getBytes())
      val output = new ByteArrayOutputStream()

      Console.withIn(input) {
        Console.withOut(new PrintStream(output)) {
          tui.handleCardSelection("10")
        }
      }

      val consoleOut = output.toString
      consoleOut should include("Invalid card index")
    }
  }
}