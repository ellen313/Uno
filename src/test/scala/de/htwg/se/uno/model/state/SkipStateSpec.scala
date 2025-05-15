import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.se.uno.model._
import de.htwg.se.uno.model.state._

class SkipStateSpec extends AnyWordSpec with Matchers {

  "SkipState" should {

    "call nextPlayer twice and change state to PlayerTurnState" in {
      val dummyGameState = new GameState(List(), 0, List(), false, List(), List()) {
        private var counter = 0
        override def nextPlayer(): GameState = {
          counter += 1
          if (counter < 2) this else this.copy(currentPlayerIndex = (currentPlayerIndex + 2) % 4)
        }
      }

      val unoStates = new UnoStates(dummyGameState)
      val skipState = SkipState(unoStates)

      val resultState = skipState.nextPlayer()

      resultState.getClass.getSimpleName shouldBe "PlayerTurnState"
      unoStates.gameState.currentPlayerIndex shouldBe 2
    }

    "return this on playCard" in {
      val dummyGameState = new GameState(List(), 0, List(), false, List(), List())
      val unoStates = new UnoStates(dummyGameState)
      val skipState = SkipState(unoStates)
      skipState.playCard() shouldBe skipState
    }

    "return this on drawCard" in {
      val dummyGameState = new GameState(List(), 0, List(), false, List(), List())
      val unoStates = new UnoStates(dummyGameState)
      val skipState = SkipState(unoStates)
      skipState.drawCard() shouldBe skipState
    }

    "return this on dealInitialCards" in {
      val dummyGameState = new GameState(List(), 0, List(), false, List(), List())
      val unoStates = new UnoStates(dummyGameState)
      val skipState = SkipState(unoStates)
      skipState.dealInitialCards() shouldBe skipState
    }

    "return this on checkForWinner" in {
      val dummyGameState = new GameState(List(), 0, List(), false, List(), List())
      val unoStates = new UnoStates(dummyGameState)
      val skipState = SkipState(unoStates)
      skipState.checkForWinner() shouldBe skipState
    }

    "return this on playerSaysUno" in {
      val dummyGameState = new GameState(List(), 0, List(), false, List(), List())
      val unoStates = new UnoStates(dummyGameState)
      val skipState = SkipState(unoStates)
      skipState.playerSaysUno() shouldBe skipState
    }

    "isValidPlay should be false" in {
      val dummyGameState = new GameState(List(), 0, List(), false, List(), List())
      val unoStates = new UnoStates(dummyGameState)
      val skipState = SkipState(unoStates)
      skipState.isValidPlay shouldBe false
    }
  }
}
