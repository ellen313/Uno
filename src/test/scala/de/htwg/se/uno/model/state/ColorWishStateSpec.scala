import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.se.uno.model._
import de.htwg.se.uno.model.state._

class ColorWishStateSpec extends AnyWordSpec with Matchers {

  "ColorWishState" should {

    "transition to PlayerTurnState on playCard" in {
      val dummyGameState = new GameState(List(), 0, List(), false, List(), List())
      val unoStates = new UnoStates(dummyGameState)
      val colorWishState = ColorWishState(unoStates)

      val result = colorWishState.playCard()

      result.getClass.getSimpleName shouldBe "PlayerTurnState"
    }

    "return this on drawCard" in {
      val dummyGameState = new GameState(List(), 0, List(), false, List(), List())
      val unoStates = new UnoStates(dummyGameState)
      val colorWishState = ColorWishState(unoStates)

      colorWishState.drawCard() shouldBe colorWishState
    }

    "return this on nextPlayer" in {
      val dummyGameState = new GameState(List(), 0, List(), false, List(), List())
      val unoStates = new UnoStates(dummyGameState)
      val colorWishState = ColorWishState(unoStates)

      colorWishState.nextPlayer() shouldBe colorWishState
    }

    "return this on dealInitialCards" in {
      val dummyGameState = new GameState(List(), 0, List(), false, List(), List())
      val unoStates = new UnoStates(dummyGameState)
      val colorWishState = ColorWishState(unoStates)

      colorWishState.dealInitialCards() shouldBe colorWishState
    }

    "return this on checkForWinner" in {
      val dummyGameState = new GameState(List(), 0, List(), false, List(), List())
      val unoStates = new UnoStates(dummyGameState)
      val colorWishState = ColorWishState(unoStates)

      colorWishState.checkForWinner() shouldBe colorWishState
    }

    "return this on playerSaysUno" in {
      val dummyGameState = new GameState(List(), 0, List(), false, List(), List())
      val unoStates = new UnoStates(dummyGameState)
      val colorWishState = ColorWishState(unoStates)

      colorWishState.playerSaysUno() shouldBe colorWishState
    }

    "isValidPlay should be false" in {
      val dummyGameState = new GameState(List(), 0, List(), false, List(), List())
      val unoStates = new UnoStates(dummyGameState)
      val colorWishState = ColorWishState(unoStates)

      colorWishState.isValidPlay shouldBe false
    }
  }
}
