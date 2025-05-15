import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.se.uno.controller.command.ColorWishCommand
import de.htwg.se.uno.controller.GameBoard
import de.htwg.se.uno.model.GameState

class ColorWishCommandSpec extends AnyWordSpec with Matchers {

  "ColorWishCommand" should {

    "set the selected color in GameBoard and notify observers" in {
      val initialGameState = GameState(
        players = List(),
        currentPlayerIndex = 0,
        allCards = List(),
        isReversed = false,
        discardPile = List(),
        drawPile = List(),
        selectedColor = None
      )
      GameBoard.updateState(initialGameState)

      val color = "red"
      ColorWishCommand(color).execute()

      GameBoard.gameState.selectedColor shouldBe None
    }
  }
}
