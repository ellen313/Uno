import
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec

class GameBoardSpec extends AnyWordSpec {
  "A gameBoard" when {
    "shuffled" should {
      "return a new gameBoard with shuffled cards" in {
        val initialCards = List(Card("green", 8), Card("red", 6))
        val gameBoard = GameBoard(initialCards)
        val shuffledGameBoard = gameBoard.shuffle()

        shuffledGameBoard.cards should not be initialCards
      }
    }
    "drawn" should {
      "return a tuple with the drawn card an the updated GameBoard" in {
        val initialCards = List(Card("blue", 9), Card("red", 2))
        val gameBoard = GameBoard(initialCards)
        val (drawnCard, updatedGameBoard) = gameBoard.draw()

        updatedGameBoard.cards should not contain drawnCard
      }
    }
  }

  "A gameState" when {
    "printed" should {
      "print the game condition correctly" in {
        val playerHand = PlayerHand(List(Card("red", 8), Card("blue", 10)))
        val playerStacks = List(List(Card("green", 12)), List(Card("blue", 9)))
        val centerStack = List(Card("blue", 6))
        val drawPile = List(Card("green", 3))
        val discardPile = List(Card("red", 8))

        val gameBoard = GameBoard(playerStacks, centerStack, drawPile, discardPile)
        val gameState = GameState(List(playerHand), gameBoard, currentPlayerIndex = 0)

        val printedOutput: Unit = printGameBoard(gameState)
        printedOutput shouldEqual expectedOutput //define expected output
      }
    }
  }
}
