import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.se.uno.model._
import de.htwg.se.uno.model.state._

class UnoCalledStateSpec extends AnyWordSpec with Matchers {

  class DummyPlayerTurnState(context: UnoStates) extends GamePhase {
    override def playCard(): GamePhase = this
    override def drawCard(): GamePhase = this
    override def nextPlayer(): GamePhase = this
    override def dealInitialCards(): GamePhase = this
    override def checkForWinner(): GamePhase = this
    override def playerSaysUno(): GamePhase = this
    override def isValidPlay: Boolean = true
  }

  "UnoCalledState" should {

    "handle playerSaysUno by updating gamestate and switching to PlayerTurnState" in {
      val dummyGameState = new GameState(
        players = List(PlayerHand(List()), PlayerHand(List())),
        currentPlayerIndex = 0,
        allCards = List(),
        isReversed = false,
        discardPile = List(),
        drawPile = List()
      ) {
        override def playerSaysUno(playerIndex: Int): GameState = {
          this
        }
      }

      val unoStates = new UnoStates(dummyGameState)
      val unoCalledState = UnoCalledState(unoStates)

      unoStates.setState(unoCalledState)
      
      var setStateCalledWith: Option[GamePhase] = None
      val unoStatesSpy = new UnoStates(dummyGameState) {
        override def setState(state: GamePhase): Unit = {
          setStateCalledWith = Some(state)
          super.setState(state)
        }
      }

      val unoCalledStateSpy = UnoCalledState(unoStatesSpy)
      unoStatesSpy.setState(unoCalledStateSpy)
      
      val resultState = unoCalledStateSpy.playerSaysUno()
      
      setStateCalledWith should not be None
      setStateCalledWith.get.getClass.getSimpleName shouldBe "PlayerTurnState"
      resultState shouldBe setStateCalledWith.get
    }
    
    "playCard should return this" in {
      val dummyGameState = new GameState(List(), 0, List(), false, List(), List())
      val unoStates = new UnoStates(dummyGameState)
      val state = UnoCalledState(unoStates)
      state.playCard() shouldBe state
    }

    "drawCard should return this" in {
      val dummyGameState = new GameState(List(), 0, List(), false, List(), List())
      val unoStates = new UnoStates(dummyGameState)
      val state = UnoCalledState(unoStates)
      state.drawCard() shouldBe state
    }

    "nextPlayer should return this" in {
      val dummyGameState = new GameState(List(), 0, List(), false, List(), List())
      val unoStates = new UnoStates(dummyGameState)
      val state = UnoCalledState(unoStates)
      state.nextPlayer() shouldBe state
    }

    "dealInitialCards should return this" in {
      val dummyGameState = new GameState(List(), 0, List(), false, List(), List())
      val unoStates = new UnoStates(dummyGameState)
      val state = UnoCalledState(unoStates)
      state.dealInitialCards() shouldBe state
    }

    "checkForWinner should return this" in {
      val dummyGameState = new GameState(List(), 0, List(), false, List(), List())
      val unoStates = new UnoStates(dummyGameState)
      val state = UnoCalledState(unoStates)
      state.checkForWinner() shouldBe state
    }

    "isValidPlay should be false" in {
      val dummyGameState = new GameState(List(), 0, List(), false, List(), List())
      val unoStates = new UnoStates(dummyGameState)
      val state = UnoCalledState(unoStates)
      state.isValidPlay shouldBe false
    }
  }
}
