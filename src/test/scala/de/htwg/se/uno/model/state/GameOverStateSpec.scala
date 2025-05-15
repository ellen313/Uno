package de.htwg.se.uno.model.state

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.se.uno.model.GameState

class GameOverStateSpec extends AnyWordSpec with Matchers {

  "GameOverState" should {

    val dummyGameState = GameState(Nil, 0, Nil, false, Nil, Nil)
    val unoStates = new UnoStates(dummyGameState)
    val gameOverState = GameOverState(unoStates)

    "return this on playCard" in {
      gameOverState.playCard() shouldBe gameOverState
    }

    "return this on drawCard" in {
      gameOverState.drawCard() shouldBe gameOverState
    }

    "return this on nextPlayer" in {
      gameOverState.nextPlayer() shouldBe gameOverState
    }

    "return this on dealInitialCards" in {
      gameOverState.dealInitialCards() shouldBe gameOverState
    }

    "return this on checkForWinner" in {
      gameOverState.checkForWinner() shouldBe gameOverState
    }

    "return this on playerSaysUno" in {
      gameOverState.playerSaysUno() shouldBe gameOverState
    }

    "have isValidPlay false" in {
      gameOverState.isValidPlay shouldBe false
    }
  }
}
