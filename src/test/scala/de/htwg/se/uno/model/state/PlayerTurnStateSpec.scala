import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.se.uno.model._
import de.htwg.se.uno.model.state._

class PlayerTurnStateSpec extends AnyWordSpec with Matchers {

  "PlayerTurnState" should {

    "call nextPlayer and return the current state" in {
      val dummyGameState = new GameState(List(), 0, List(), false, List(), List()) {
        override def nextPlayer(): GameState = this.copy(currentPlayerIndex = (currentPlayerIndex + 1) % 4)
      }

      val unoStates = new UnoStates(dummyGameState)
      val state = PlayerTurnState(unoStates)
      unoStates.setState(state)

      val newState = state.nextPlayer()

      newState shouldBe state
      unoStates.gameState.currentPlayerIndex shouldBe 1
    }

    "return this on playCard" in {
      val unoStates = new UnoStates(GameState(List(), 0, List(), false, List(), List()))
      val state = PlayerTurnState(unoStates)
      state.playCard() shouldBe state
    }

    "return this on drawCard" in {
      val unoStates = new UnoStates(GameState(List(), 0, List(), false, List(), List()))
      val state = PlayerTurnState(unoStates)
      state.drawCard() shouldBe state
    }

    "return this on dealInitialCards" in {
      val unoStates = new UnoStates(GameState(List(), 0, List(), false, List(), List()))
      val state = PlayerTurnState(unoStates)
      state.dealInitialCards() shouldBe state
    }

    "return this on checkForWinner" in {
      val unoStates = new UnoStates(GameState(List(), 0, List(), false, List(), List()))
      val state = PlayerTurnState(unoStates)
      state.checkForWinner() shouldBe state
    }

    "return this on playerSaysUno" in {
      val unoStates = new UnoStates(GameState(List(), 0, List(), false, List(), List()))
      val state = PlayerTurnState(unoStates)
      state.playerSaysUno() shouldBe state
    }

    "isValidPlay should be false" in {
      val unoStates = new UnoStates(GameState(List(), 0, List(), false, List(), List()))
      val state = PlayerTurnState(unoStates)
      state.isValidPlay shouldBe false
    }
  }
}
