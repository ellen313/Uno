package model

import model.*
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

class GameStateSpec extends AnyWordSpec {

  "GameState" should {

    val dummyGameState: GameState = GameState(
      players = List(),
      currentPlayerIndex = 0,
      allCards = List(),
      isReversed = false,
      discardPile = List(),
      drawPile = List()
    )

    val redOne = NumberCard("red", 1)
    val redTwo = NumberCard("red", 2)
    val blueOne = NumberCard("blue", 1)
    val skipRed = ActionCard("red", "skip")
    val reverseBlue = ActionCard("blue", "reverse")
    val drawTwoGreen = ActionCard("green", "draw two")
    val wild = WildCard("wild")
    val wildDrawFour = WildCard("wild draw four")

    def baseState(players: Int = 3): GameState = {
      GameState(
        players = List.fill(players)(PlayerHand(List.empty, false)),
        currentPlayerIndex = 0,
        allCards = List(redOne, redTwo, blueOne, skipRed, reverseBlue, drawTwoGreen, wild, wildDrawFour),
        discardPile = List(redOne),
        drawPile = List(redTwo, blueOne, skipRed, reverseBlue, drawTwoGreen, wild, wildDrawFour)
      )
    }

    //nextPlayer
    "return the previous player when isReversed is true" in {
      val player1 = PlayerHand(List(), hasSaidUno = false)
      val player2 = PlayerHand(List(), hasSaidUno = false)
      val player3 = PlayerHand(List(), hasSaidUno = false)

      val gameState = GameState(
        players = List(player1, player2, player3),
        currentPlayerIndex = 0,
        allCards = List(),
        isReversed = true,
        discardPile = List(),
        drawPile = List()
      )

      val next = gameState.nextPlayer()

      next.currentPlayerIndex shouldBe 2
    }

    "return the next player when isReversed is false" in {
      val player1 = PlayerHand(List(), hasSaidUno = false)
      val player2 = PlayerHand(List(), hasSaidUno = false)
      val player3 = PlayerHand(List(), hasSaidUno = false)

      val gameState = GameState(
        players = List(player1, player2, player3),
        currentPlayerIndex = 1,
        allCards = List(),
        isReversed = false,
        discardPile = List(),
        drawPile = List()
      )

      val next = gameState.nextPlayer()

      next.currentPlayerIndex shouldBe 2
    }

    //checkForWinner
    "return Some(index) when a player has no cards" in {
      val winningHand = PlayerHand(List(), hasSaidUno = false)
      val otherHand = PlayerHand(List(NumberCard("red", 5)), hasSaidUno = false)

      val gameState = GameState(
        players = List(otherHand, winningHand),
        currentPlayerIndex = 0,
        allCards = List(),
        isReversed = false,
        discardPile = List(),
        drawPile = List()
      )

      gameState.checkForWinner() shouldBe Some(1)
    }

    "return None when no player has empty hand" in {
      val player1 = PlayerHand(List(NumberCard("red", 2)), hasSaidUno = false)
      val player2 = PlayerHand(List(NumberCard("blue", 9)), hasSaidUno = false)

      val gameState = GameState(
        players = List(player1, player2),
        currentPlayerIndex = 0,
        allCards = List(),
        isReversed = false,
        discardPile = List(),
        drawPile = List()
      )

      gameState.checkForWinner() shouldBe None
    }

    //playerSaysUno
    "update the hasSaidUno status of the player to true" in {
      val player1 = PlayerHand(List(NumberCard("red", 5)), hasSaidUno = false)
      val player2 = PlayerHand(List(NumberCard("blue", 7)), hasSaidUno = false)

      val gameState = GameState(
        players = List(player1, player2),
        currentPlayerIndex = 0,
        allCards = List(),
        isReversed = false,
        discardPile = List(),
        drawPile = List()
      )

      val updatedGameState = gameState.playerSaysUno(0)

      updatedGameState.players(0).hasSaidUno shouldBe true
    }


    "drawCard" should {
      "throw exception when both draw and discard piles are too small" in {
        val player = PlayerHand(List(), hasSaidUno = false)
        val gameState = GameState(
          players = List(player),
          currentPlayerIndex = 0,
          allCards = List(),
          isReversed = false,
          discardPile = List(NumberCard("red", 3)),
          drawPile = List()
        )

        assertThrows[RuntimeException] {
          gameState.drawCard(player, List(), List(NumberCard("red", 3)))
        }
      }

      "reshuffle discard pile into draw pile if draw pile is empty" in {
        val player = PlayerHand(List(), hasSaidUno = false)
        val cardToDraw = NumberCard("blue", 5)
        val discardPile = List(cardToDraw, NumberCard("red", 3), NumberCard("green", 7))

        val gameState = GameState(
          players = List(player),
          currentPlayerIndex = 0,
          allCards = List(),
          isReversed = false,
          discardPile = discardPile,
          drawPile = List()
        )

        val (_, updatedHand, newDrawPile, _) = gameState.drawCard(player, List(), discardPile)
        updatedHand.cards.size shouldBe 1
        newDrawPile.size shouldBe 1
      }
    }

    //playCard
    "GameState.playCard" should {

      "return unchanged state if any player has empty hand" in {
        val state = baseState().copy(players = List(
          PlayerHand(List(redOne), false),
          PlayerHand(List.empty, false)
        ))
        state.playCard(redOne) shouldBe state
      }

      "stop drawing after max iterations" in {
        val state = baseState().copy(
          players = List(PlayerHand(List(blueOne), false)),
          drawPile = List.fill(10)(blueOne)
        )
        val result = state.playCard(blueOne)
        result.discardPile should contain(blueOne)
      }

      "update UNO status when player has UNO" in {
        val state = baseState().copy(
          players = List(PlayerHand(List(redOne), true))
        )
        val result = state.playCard(redOne)
        result.players.head.hasSaidUno shouldBe false
      }


      "handle skip card correctly by skipping the next player" in {
        val player1 = PlayerHand(List(skipRed), false)
        val player2 = PlayerHand(List(redOne), false)
        val player3 = PlayerHand(List(blueOne), false)

        val gameState = GameState(
          players = List(player1, player2, player3),
          currentPlayerIndex = 0,
          allCards = List(skipRed, redOne, blueOne),
          discardPile = List(redOne),
          drawPile = List()
        )

        val result = gameState.playCard(skipRed)
        result.currentPlayerIndex shouldBe 2 // should skip player 1 (index 1)
      }

      "handle reverse card correctly by reversing direction" in {
        val player1 = PlayerHand(List(reverseBlue), false)
        val player2 = PlayerHand(List(redOne), false)

        val gameState = GameState(
          players = List(player1, player2),
          currentPlayerIndex = 0,
          allCards = List(reverseBlue, redOne),
          isReversed = false,
          discardPile = List(redOne),
          drawPile = List()
        )

        val result = gameState.playCard(reverseBlue)
        result.isReversed shouldBe true
        result.currentPlayerIndex shouldBe 1
      }

      "handle draw two card correctly by making next player draw 2 cards" in {
        val player1 = PlayerHand(List(drawTwoGreen), false)
        val player2 = PlayerHand(List(redOne), false)

        val gameState = GameState(
          players = List(player1, player2),
          currentPlayerIndex = 0,
          allCards = List(drawTwoGreen, redOne, blueOne, skipRed),
          discardPile = List(redOne),
          drawPile = List(blueOne, skipRed) // 2 cards available
        )

        val result = gameState.playCard(drawTwoGreen)
        result.players(1).cards should have size 3 // original 1 + 2 drawn
        result.drawPile shouldBe empty
        result.currentPlayerIndex shouldBe 1
      }

      "handle wild draw four card correctly by making next player draw 4 cards" in {
        val player1 = PlayerHand(List(wildDrawFour), false)
        val player2 = PlayerHand(List(redOne), false)

        val gameState = GameState(
          players = List(player1, player2),
          currentPlayerIndex = 0,
          allCards = List(wildDrawFour, redOne, blueOne, skipRed, reverseBlue, drawTwoGreen),
          discardPile = List(redOne),
          drawPile = List(blueOne, skipRed, reverseBlue, drawTwoGreen) // exactly 4 cards
        )

        val result = gameState.playCard(wildDrawFour)
        result.players(1).cards should have size 5 // original 1 + 4 drawn
        result.drawPile shouldBe empty
        result.currentPlayerIndex shouldBe 1
      }

      "handle normal number card correctly by just moving to next player" in {
        val player1 = PlayerHand(List(redTwo), false)
        val player2 = PlayerHand(List(blueOne), false)

        val gameState = GameState(
          players = List(player1, player2),
          currentPlayerIndex = 0,
          allCards = List(redTwo, blueOne),
          discardPile = List(redOne),
          drawPile = List()
        )

        val result = gameState.playCard(redTwo)
        result.currentPlayerIndex shouldBe 1
        result.isReversed shouldBe false // direction unchanged
        result.discardPile should contain(redTwo)
      }
    }


    //isValidPlay
    "isValidPlay" should {
      "return true when topCard is None" in {
        val card = NumberCard("red", 5)
        val dummyPlayerHand = PlayerHand(List(card), hasSaidUno = false)
        val gameState = GameState(
          players = List(dummyPlayerHand),
          currentPlayerIndex = 0,
          allCards = List.empty,
          discardPile = List.empty,
          drawPile = List.empty
        )

        val result = gameState.isValidPlay(card, None)
        result shouldBe true
      }

      "return false when both cards are Wild Draw Four" in {
        val card = WildCard("wild draw four")
        val topCard = Some(WildCard("wild draw four"))
        dummyGameState.isValidPlay(card, topCard) shouldBe false
      }

      "return true for WildCard(wild) against any card" in {
        val card = WildCard("wild")
        val topCard = Some(NumberCard("red", 3))
        dummyGameState.isValidPlay(card, topCard) shouldBe true
      }

      "return true for WildCard(wild draw four) against any card" in {
        val card = WildCard("wild draw four")
        val topCard = Some(ActionCard("green", "reverse"))
        dummyGameState.isValidPlay(card, topCard) shouldBe true
      }

      "return true when any card is played on top of Wild(wild)" in {
        val card = NumberCard("yellow", 9)
        val topCard = Some(WildCard("wild"))
        dummyGameState.isValidPlay(card, topCard) shouldBe true
      }

      "return true when any card is played on top of Wild(wild draw four)" in {
        val card = ActionCard("blue", "skip")
        val topCard = Some(WildCard("wild draw four"))
        dummyGameState.isValidPlay(card, topCard) shouldBe true
      }

      "return true when Draw Two cards match by color" in {
        val card = ActionCard("red", "draw two")
        val topCard = Some(ActionCard("red", "draw two"))
        dummyGameState.isValidPlay(card, topCard) shouldBe true
      }

      "return true when NumberCards match by color" in {
        val card = NumberCard("green", 5)
        val topCard = Some(NumberCard("green", 9))
        dummyGameState.isValidPlay(card, topCard) shouldBe true
      }

      "return true when NumberCards match by number" in {
        val card = NumberCard("blue", 7)
        val topCard = Some(NumberCard("red", 7))
        dummyGameState.isValidPlay(card, topCard) shouldBe true
      }

      "return true when NumberCard is played on ActionCard with same color" in {
        val card = NumberCard("yellow", 4)
        val topCard = Some(ActionCard("yellow", "reverse"))
        dummyGameState.isValidPlay(card, topCard) shouldBe true
      }

      "return true when ActionCard is played on NumberCard with same color" in {
        val card = ActionCard("green", "skip")
        val topCard = Some(NumberCard("green", 2))
        dummyGameState.isValidPlay(card, topCard) shouldBe true
      }

      "return true when ActionCards match by color" in {
        val card = ActionCard("blue", "skip")
        val topCard = Some(ActionCard("blue", "reverse"))
        dummyGameState.isValidPlay(card, topCard) shouldBe true
      }

      "return true when ActionCards match by action" in {
        val card = ActionCard("yellow", "skip")
        val topCard = Some(ActionCard("green", "skip"))
        dummyGameState.isValidPlay(card, topCard) shouldBe true
      }

      "return false when no rule matches" in {
        val card = NumberCard("green", 2)
        val topCard = Some(NumberCard("red", 9))
        dummyGameState.isValidPlay(card, topCard) shouldBe false
      }

      "reject a card with an invalid wildcard type and top card" in {
        val topCard = NumberCard("blue", 5)
        val invalidWildCard = WildCard("unknown")
        dummyGameState.isValidPlay(invalidWildCard, Some(topCard)) shouldBe false
      }

      "should trigger sayUno when player has one card left after playing a valid card" in {
        val startingCards = List(
          NumberCard("red", 3),
          NumberCard("blue", 5)
        )
        val playerHand = PlayerHand(startingCards)
        val gameState = GameState(
          players = List(playerHand),
          currentPlayerIndex = 0,
          allCards = Nil,
          isReversed = false,
          discardPile = List(NumberCard("red", 7)),
          drawPile = Nil
        )

        val updatedGameState = gameState.playCard(NumberCard("red", 3))
        val updatedPlayer = updatedGameState.players.head

        updatedPlayer.cards should have size 1
        updatedPlayer.hasSaidUno shouldBe true
      }
    }

    //notifyObservers
    "directly calling notifyObservers" should {
      "trigger observer update" in {
        var wasNotified = false

        val gameState = GameState(
          players = List.empty,
          currentPlayerIndex = 0,
          allCards = List.empty,
          discardPile = List.empty,
          drawPile = List.empty
        )

        gameState.addObserver(new Observer {
          override def update(): Unit = wasNotified = true
        })

        gameState.notifyObservers()

        wasNotified shouldBe true
      }
    }
  }
}
