import org.scalatest.wordspec.AnyWordSpec
import de.htwg.se.uno.model._
import de.htwg.se.uno.controller.command.ColorWishCommand
import de.htwg.se.uno.controller.GameBoard

class ColorWishCommandSpec extends AnyWordSpec {
  "ColorWishCommand" should {
    "set the selected color in the game state and notify observers" in {
      val playerHand = PlayerHand(List())
      val gameState = GameState(
        players = List(playerHand),
        currentPlayerIndex = 0,
        allCards = List(),
        isReversed = false,
        discardPile = List(),
        drawPile = List(),
        selectedColor = None
      )
      val gameBoard = GameBoard(gameState, List(), List())

      val colorWishCommand = ColorWishCommand(gameBoard, "green")
      colorWishCommand.execute()
    }

    "print the correct output when the color wish command is executed" in {
      val playerHand = PlayerHand(List())
      val gameState = GameState(
        players = List(playerHand),
        currentPlayerIndex = 0,
        allCards = List(),
        isReversed = false,
        discardPile = List(),
        drawPile = List(),
        selectedColor = None
      )

      val gameBoard = GameBoard(gameState, List(), List())

      val colorWishCommand = ColorWishCommand(gameBoard, "green")

      val outStream = new java.io.ByteArrayOutputStream()
      Console.withOut(outStream) {
        colorWishCommand.execute()
      }
      assert(outStream.toString.contains("Farbe für Wild Card gesetzt: green"))
    }
  }
}
