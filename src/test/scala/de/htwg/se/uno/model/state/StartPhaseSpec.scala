import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.se.uno.model._
import de.htwg.se.uno.model.state._

class StartPhaseSpec extends AnyWordSpec with Matchers {

  "StartState" should {

    "return this on playCard" in {
      val dummyGameState = new GameState(List(), 0, List(), false, List(), List()) {
        override def dealInitialCards(n: Int): GameState = this
      }
      val unoStates = new UnoPhases(dummyGameState)
      val startState = StartPhase(unoStates)
      startState.playCard() shouldBe startState
    }

    "return this on drawCard" in {
      val dummyGameState = new GameState(List(), 0, List(), false, List(), List()) {
        override def dealInitialCards(n: Int): GameState = this
      }
      val unoStates = new UnoPhases(dummyGameState)
      val startState = StartPhase(unoStates)
      startState.drawCard() shouldBe startState
    }

    "return this on nextPlayer" in {
      val dummyGameState = new GameState(List(), 0, List(), false, List(), List()) {
        override def dealInitialCards(n: Int): GameState = this
      }
      val unoStates = new UnoPhases(dummyGameState)
      val startState = StartPhase(unoStates)
      startState.nextPlayer() shouldBe startState
    }

    "dealInitialCards sets the gameState and changes state to PlayerTurnState" in {
      val dummyGameState = new GameState(List(), 0, List(), false, List(), List()) {
        override def dealInitialCards(n: Int): GameState = this.copy(discardPile = List(NumberCard("red", 1)))
      }

      val unoStates = new UnoPhases(dummyGameState) {
        override def setState(state: GamePhase): Unit = super.setState(state)
      }

      val startState = StartPhase(unoStates)

      val returnedState = startState.dealInitialCards()

      unoStates.gameState.discardPile should contain (NumberCard("red", 1))
      returnedState.getClass.getSimpleName shouldBe "PlayerTurnState"
    }

    "return this on checkForWinner" in {
      val dummyGameState = new GameState(List(), 0, List(), false, List(), List())
      val unoStates = new UnoPhases(dummyGameState)
      val startState = StartPhase(unoStates)
      startState.checkForWinner() shouldBe startState
    }

    "return this on playerSaysUno" in {
      val dummyGameState = new GameState(List(), 0, List(), false, List(), List())
      val unoStates = new UnoPhases(dummyGameState)
      val startState = StartPhase(unoStates)
      startState.playerSaysUno() shouldBe startState
    }

    "isValidPlay should be false" in {
      val dummyGameState = new GameState(List(), 0, List(), false, List(), List())
      val unoStates = new UnoPhases(dummyGameState)
      val startState = StartPhase(unoStates)
      startState.isValidPlay shouldBe false
    }
  }
}
