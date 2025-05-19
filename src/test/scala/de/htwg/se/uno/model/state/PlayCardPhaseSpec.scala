package de.htwg.se.uno.model.state

import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers
import de.htwg.se.uno.model.*

class PlayCardPhaseSpec extends AnyWordSpec with Matchers {

  "PlayCardState" should {

    "play a normal card and move to PlayerTurnState" in {
      val card = NumberCard("red", 5)
      val player = PlayerHand(List(card))
      val gameState = GameState(List(player), 0, List(), isReversed = false, List(card), List())
      val context = new UnoPhases(gameState)

      val state = PlayCardPhase(context, card)
      val nextState = state.playCard()

      nextState shouldBe a[PlayerTurnPhase]
    }

    "play a skip card and skip the next player" in {
      val card = ActionCard("red", "skip")
      val players = List(PlayerHand(List(card)), PlayerHand(Nil), PlayerHand(Nil))
      val gameState = GameState(players, 0, List(), false, List(card), List())
      val context = new UnoPhases(gameState)

      val state = PlayCardPhase(context, card)
      val result = state.playCard()

      result shouldBe a[PlayerTurnPhase]
      context.gameState.currentPlayerIndex shouldBe 2
    }

    "play a reverse card and toggle isReversed" in {
      val card = ActionCard("blue", "reverse")
      val players = List(PlayerHand(List(card)), PlayerHand(Nil))
      val gameState = GameState(players, 0, List(), false, List(card), List())
      val context = new UnoPhases(gameState)

      val state = PlayCardPhase(context, card)
      val result = state.playCard()

      result shouldBe a[PlayerTurnPhase]
      context.gameState.isReversed shouldBe true
    }

    "play a wild card and change to next player" in {
      val card = WildCard("wild")
      val players = List(PlayerHand(List(card)), PlayerHand(Nil))
      val gameState = GameState(players, 0, List(), false, List(card), List())
      val context = new UnoPhases(gameState)

      val state = PlayCardPhase(context, card)
      val result = state.playCard()

      result shouldBe a[PlayerTurnPhase]
      context.gameState.currentPlayerIndex shouldBe 1
    }

    "drawCard should return itself and print" in {
      val card = NumberCard("red", 1)
      val context = new UnoPhases(GameState(List(PlayerHand(List(card))), 0, Nil, false, Nil, Nil))
      val state = PlayCardPhase(context, card)

      state.drawCard() shouldBe state
    }

    "dealInitialCards should return itself and print" in {
      val card = NumberCard("red", 1)
      val context = new UnoPhases(GameState(List(PlayerHand(List(card))), 0, Nil, false, Nil, Nil))
      val state = PlayCardPhase(context, card)

      state.dealInitialCards() shouldBe state
    }

    "checkForWinner should return GameOverState" in {
      val card = NumberCard("red", 1)
      val context = new UnoPhases(GameState(List(PlayerHand(List(card))), 0, Nil, false, Nil, Nil))
      val state = PlayCardPhase(context, card)

      state.checkForWinner() shouldBe a[GameOverPhase]
    }

    "playerSaysUno should return UnoCalledState" in {
      val card = NumberCard("red", 1)
      val context = new UnoPhases(GameState(List(PlayerHand(List(card))), 0, Nil, false, Nil, Nil))
      val state = PlayCardPhase(context, card)

      state.playerSaysUno() shouldBe a[UnoCalledPhase]
    }

    "nextPlayer should return PlayerTurnState" in {
      val card = NumberCard("red", 1)
      val context = new UnoPhases(GameState(List(PlayerHand(List(card))), 0, Nil, false, Nil, Nil))
      val state = PlayCardPhase(context, card)

      state.nextPlayer() shouldBe a[PlayerTurnPhase]
    }

    "isValidPlay should return true if card matches" in {
      val card = NumberCard("red", 1)
      val top = NumberCard("red", 2)
      val gameState = GameState(List(PlayerHand(List(card))), 0, Nil, false, List(top), Nil)
      val context = new UnoPhases(gameState)

      val state = PlayCardPhase(context, card)
      state.isValidPlay shouldBe true
    }

    "isValidPlay should return false if card does not match" in {
      val card = NumberCard("green", 5)
      val top = NumberCard("red", 2)
      val gameState = GameState(List(PlayerHand(List(card))), 0, Nil, false, List(top), Nil)
      val context = new UnoPhases(gameState)

      val state = PlayCardPhase(context, card)
      state.isValidPlay shouldBe false
    }

    "cover draw two and wild draw four cases without exception" in {
      val drawTwoCard = ActionCard("red", "draw two")
      val wildDrawFourCard = WildCard("wild draw four")

      val player1 = PlayerHand(List(drawTwoCard, wildDrawFourCard))
      val player2 = PlayerHand(Nil)

      val dummyPenaltyCards = List.fill(20)(NumberCard("green", 1))

      val allCards = player1.cards ++ player2.cards ++ dummyPenaltyCards :+ drawTwoCard :+ wildDrawFourCard

      val stateDrawTwo = {
        val gameState = GameState(
          players = List(player1, player2),
          currentPlayerIndex = 0,
          allCards = allCards,
          isReversed = false,
          discardPile = List(drawTwoCard),
          drawPile = dummyPenaltyCards,
          selectedColor = None
        )
        val context = new UnoPhases(gameState)
        PlayCardPhase(context, drawTwoCard)
      }
      noException should be thrownBy stateDrawTwo.playCard()

      val stateWildDrawFour = {
        val gameState = GameState(
          players = List(player1, player2),
          currentPlayerIndex = 0,
          allCards = allCards,
          isReversed = false,
          discardPile = List(wildDrawFourCard),
          drawPile = dummyPenaltyCards,
          selectedColor = Some("blue")
        )
        val context = new UnoPhases(gameState)
        PlayCardPhase(context, wildDrawFourCard)
      }
      noException should be thrownBy stateWildDrawFour.playCard()
    }


    "handle penalty cards correctly when game is reversed" in {
      val card = ActionCard("red", "draw two")
      val players = List(
        PlayerHand(List(card)),
        PlayerHand(List(NumberCard("blue", 1))),
        PlayerHand(List(NumberCard("green", 2)))
      )
      val drawPile = List(NumberCard("yellow", 3), NumberCard("red", 4))
      val gameState = GameState(
        players = players,
        currentPlayerIndex = 0,
        allCards = players.flatMap(_.cards) ++ drawPile,
        isReversed = true,
        discardPile = List(card),
        drawPile = drawPile,
        selectedColor = None
      )
      val context = new UnoPhases(gameState)

      val state = PlayCardPhase(context, card)
      val result = state.playCard()

      result shouldBe a[PlayerTurnPhase]
      context.gameState.currentPlayerIndex shouldBe 2
      context.gameState.players(2).cards should have size 3
      context.gameState.drawPile shouldBe empty
    }
  }
}
