package de.htwg.se.uno.model.gameComponent.base

import de.htwg.se.uno.model.cardComponent.{ActionCard, Card, NumberCard, WildCard}
import de.htwg.se.uno.model.playerComponent.PlayerHand
import de.htwg.se.uno.model.gameComponent.{Failure, Success}
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers

class GameStateSpec extends AnyWordSpec with Matchers {

  val red5: NumberCard = NumberCard("red", 5)
  val blue5: NumberCard = NumberCard("blue", 5)
  val redDraw2: ActionCard = ActionCard("red", "draw two")
  val wildCard: WildCard = WildCard("wild")
  val drawFour: WildCard = WildCard("wild draw four")

  val allCards: List[Card] = List(red5, blue5, redDraw2, wildCard, drawFour)
  val player1: PlayerHand = PlayerHand(List(red5))
  val player2: PlayerHand = PlayerHand(List(blue5))
  val initialPlayers: List[PlayerHand] = List(player1, player2)

  val initialState: GameState = GameState(
    players = initialPlayers,
    currentPlayerIndex = 0,
    allCards = allCards,
    discardPile = List(redDraw2),
    drawPile = List(wildCard, red5, blue5),
    isReversed = false
  )

  "GameState" should {

    "switch to the next player" in {
      val nextState = initialState.nextPlayer()
      nextState.currentPlayerIndex shouldBe 1
    }

    "deal initial cards to all players" in {
      val state = initialState.copy(drawPile = List.fill(10)(red5))
      val updatedState = state.dealInitialCards(2)
      updatedState.players.foreach(_.cards.length shouldBe 3)
      updatedState.drawPile.length shouldBe 6
    }

    "identify a winner if a player has no cards" in {
      val winningState = initialState.copy(players = List(PlayerHand(Nil), player2))
      winningState.checkForWinner() shouldBe Some(0)
    }

    "draw a card and return updated state" in {
      val (newState, drawnCard) = initialState.drawCardAndReturnDrawn()
      newState.players.head.cards should contain(drawnCard)
      newState.drawPile.size shouldBe initialState.drawPile.size - 1
    }

    "play a valid card and update discard pile" in {
      val updatedState = initialState.copy(players = List(PlayerHand(List(redDraw2)), player2))
        .playCard(redDraw2)

      updatedState.discardPile.head shouldBe redDraw2
      updatedState.players.head.cards shouldBe empty
    }

    "validate legal and illegal plays correctly" in {
      val topCard = Some(redDraw2)
      initialState.isValidPlay(redDraw2, topCard) shouldBe true
      initialState.isValidPlay(blue5, topCard) shouldBe false
      initialState.isValidPlay(wildCard, topCard) shouldBe true
    }

    "handle inputHandler for valid wild card play" in {
      val state = initialState.copy(
        players = List(PlayerHand(List(wildCard)), player2)
      )
      val result = state.inputHandler("play wild:0:green")
      result shouldBe a[Success]
    }

    "reject invalid input in inputHandler" in {
      val result = initialState.inputHandler("play wild:abc:green")
      result shouldBe a[Failure]
    }

    "reject out-of-bounds card index in inputHandler" in {
      val result = initialState.inputHandler("play wild:9:red")
      result shouldBe a[Failure]
    }
  }
}
