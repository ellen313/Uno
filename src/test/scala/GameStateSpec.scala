import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import model._

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
