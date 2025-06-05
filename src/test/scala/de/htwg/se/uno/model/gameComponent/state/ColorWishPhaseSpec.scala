package de.htwg.se.uno.model.gameComponent.state

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import de.htwg.se.uno.model.*
import de.htwg.se.uno.model.gameComponent.base.GameState
import de.htwg.se.uno.model.gameComponent.base.state.{ColorWishPhase, UnoPhases}
import de.htwg.se.uno.model.gameComponent.base.state.*

class ColorWishPhaseSpec extends AnyWordSpec with Matchers {

  "ColorWishState" should {

    "transition to PlayerTurnPhase on playCard" in {
      val dummyGameState = GameState(List(), 0, List(), false, List(), List())
      val unoStates = new UnoPhases(dummyGameState)
      val colorWishState = ColorWishPhase(unoStates)

      val result = colorWishState.playCard()

      result.getClass.getSimpleName shouldBe "PlayerTurnPhase"
    }

    "return this on drawCard" in {
      val dummyGameState = GameState(List(), 0, List(), false, List(), List())
      val unoStates = new UnoPhases(dummyGameState)
      val colorWishState = ColorWishPhase(unoStates)

      colorWishState.drawCard() shouldBe colorWishState
    }

    "return this on nextPlayer" in {
      val dummyGameState = GameState(List(), 0, List(), false, List(), List())
      val unoStates = new UnoPhases(dummyGameState)
      val colorWishState = ColorWishPhase(unoStates)

      colorWishState.nextPlayer() shouldBe colorWishState
    }

    "return this on dealInitialCards" in {
      val dummyGameState = GameState(List(), 0, List(), false, List(), List())
      val unoStates = new UnoPhases(dummyGameState)
      val colorWishState = ColorWishPhase(unoStates)

      colorWishState.dealInitialCards() shouldBe colorWishState
    }

    "return this on checkForWinner" in {
      val dummyGameState = GameState(List(), 0, List(), false, List(), List())
      val unoStates = new UnoPhases(dummyGameState)
      val colorWishState = ColorWishPhase(unoStates)

      colorWishState.checkForWinner() shouldBe colorWishState
    }

    "return this on playerSaysUno" in {
      val dummyGameState = GameState(List(), 0, List(), false, List(), List())
      val unoStates = new UnoPhases(dummyGameState)
      val colorWishState = ColorWishPhase(unoStates)

      colorWishState.playerSaysUno() shouldBe colorWishState
    }

    "isValidPlay should be false" in {
      val dummyGameState = GameState(List(), 0, List(), false, List(), List())
      val unoStates = new UnoPhases(dummyGameState)
      val colorWishState = ColorWishPhase(unoStates)

      colorWishState.isValidPlay shouldBe false
    }
  }
}
