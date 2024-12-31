package model

import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

class GameBoardSpec extends AnyWordSpec {

  "A GameBoard" when {
    /**test method createDeckWithAllCards*/
    "created" should {
      "generate a full deck of cards with correct composition" in {
        val gameBoard = GameBoard(Nil, Nil)
        val fullDeck = gameBoard.createDeckWithAllCards()

        fullDeck.count(_.isInstanceOf[NumberCard]) shouldEqual 76 // 19 * 4 colors (0 once, 1-9 twice)
        fullDeck.count(_.isInstanceOf[ActionCard]) shouldEqual 24 // 3 actions * 4 colors * 2 per color
        fullDeck.count(_.isInstanceOf[WildCard]) shouldEqual 8   // 4 wilds + 4 wild draw fours
        fullDeck.size shouldEqual 108
      }
    }
    //------------------------------------------------------------------------------------------------------------------
    /**test method shuffleDeck*/
    "shuffled" should {
      "create a shuffled deck with a discard pile containing the top card" in {
        val gameBoard = GameBoard(Nil, Nil).shuffleDeck()
        gameBoard.drawPile.size shouldEqual 107 // 108 total cards - 1 discard pile card
        gameBoard.discardPile.size shouldEqual 1
      }

      "return an empty discard pile when the deck is empty" in {
        val emptyDeckGameBoard = new GameBoard(Nil, Nil) {
          override def createDeckWithAllCards(): List[Card] = List.empty
        }
        val shuffledGameBoard = emptyDeckGameBoard.shuffleDeck()

        shuffledGameBoard.drawPile shouldBe empty
        shuffledGameBoard.discardPile shouldBe empty
      }
    }
    //------------------------------------------------------------------------------------------------------------------
    /**test method drawCard*/
    "a card is drawn" should {
      "return the correct card, updated player hand, and updated draw pile" in {
        val initialDrawPile = List(NumberCard("blue", 5), NumberCard("red", 3))
        val gameBoard = GameBoard(initialDrawPile, discardPile = List.empty)
        val playerHand = PlayerHand(List(NumberCard("yellow", 7)))

        val (drawnCard, updatedHand, updatedBoard) = gameBoard.drawCard(playerHand)

        drawnCard shouldEqual NumberCard("blue", 5)
        updatedHand.cards should contain(drawnCard)
        updatedBoard.drawPile shouldEqual List(NumberCard("red", 3))
      }

      "throw an exception if the draw pile is empty" in {
        val gameBoard = GameBoard(Nil, Nil)
        val playerHand = PlayerHand(Nil)

        an[RuntimeException] should be thrownBy {
          gameBoard.drawCard(playerHand)
        }
      }
    }
    //------------------------------------------------------------------------------------------------------------------
    /**test method isValidPlay*/
    "validating a play" should {
      //test case with None
      "accept any card if there is no top card" in {
        val gameBoard = GameBoard(Nil, Nil)
        val card = NumberCard("red", 5)

        gameBoard.isValidPlay(card, None) shouldBe true
      }
      //test case (WildCard("wild draw four"), WildCard("wild draw four")) => false
      "not accept two 'wild draw four' cards" in {
        val topCard = Some(WildCard("wild draw four"))
        val wildDrawFour = WildCard("wild draw four")

        val gameBoard = GameBoard(Nil, Nil)
        gameBoard.isValidPlay(wildDrawFour, topCard) shouldBe false
      }
      //test case (WildCard("wild"), _) => true
      "accept any card on a wild card" in {
        val topCard = WildCard("wild")
        val someCard = NumberCard("red", 5)

        val gameBoard = GameBoard(Nil, Nil)
        gameBoard.isValidPlay(someCard, Some(topCard)) shouldBe true
      }
      //test case (WildCard("wild draw four"), _) => true
      "accept any card on a wild draw four" in {
        val topCard = WildCard("wild draw four")
        val someCard = NumberCard("yellow", 3)

        val gameBoard = GameBoard(Nil, Nil)
        gameBoard.isValidPlay(someCard, Some(topCard)) shouldBe true
      }
      //test case (_, WildCard("wild"))=> true
      "accept a wild card on top of any card" in {
        val topCard = NumberCard("blue", 5)
        val wildCard = WildCard("wild")

        val gameBoard = GameBoard(Nil, Nil)
        gameBoard.isValidPlay(wildCard, Some(topCard)) shouldBe true
      }
      //test case (_, WildCard("wild draw four"))=> true
      "accept a wild draw four card on top of any card" in {
        val topCard = ActionCard("red", "skip")
        val wildDrawFour = WildCard("wild draw four")

        val gameBoard = GameBoard(Nil, Nil)
        gameBoard.isValidPlay(wildDrawFour, Some(topCard)) shouldBe true
      }
      //test case (ActionCard(color, "draw two"), ActionCard(topColor, "draw two")) => color == topColor
      "accept a 'draw two' card only if the top card is the same color 'draw two'" in {
        val topCard = Some(ActionCard("red", "draw two"))
        val validCard = ActionCard("red", "draw two")
        val invalidCard = ActionCard("blue", "draw two")

        val gameBoard = GameBoard(Nil, Nil)
        gameBoard.isValidPlay(validCard, topCard) shouldBe true
        gameBoard.isValidPlay(invalidCard, topCard) shouldBe false
      }
      //test case (NumberCard(color, number), NumberCard(topColor, topNumber))=>color == topColor || number == topNumber
      "accept number cards with matching color or number" in {
        val topCard1 = NumberCard("red", 5)
        val validCard1 = NumberCard("red", 5)
        val validCard2 = NumberCard("blue", 5)
        val validCard3 = NumberCard("red", 7)
        val invalidCard = NumberCard("blue", 3)

        val gameBoard = GameBoard(Nil, Nil)
        gameBoard.isValidPlay(validCard1, Some(topCard1)) shouldBe true
        gameBoard.isValidPlay(validCard2, Some(topCard1)) shouldBe true
        gameBoard.isValidPlay(validCard3, Some(topCard1)) shouldBe true
        gameBoard.isValidPlay(invalidCard, Some(topCard1)) shouldBe false
      }

      "validate color match between NumberCard and ActionCard" in {
        val validNumberCard = NumberCard("red", 5)
        val validActionCard = ActionCard("red", "skip")
        val invalidActionCard = ActionCard("blue", "reverse")

        val gameBoard = GameBoard(Nil, Nil)

        //colors match
        gameBoard.isValidPlay(validNumberCard, Some(validActionCard)) shouldBe true

        //colors do not match
        gameBoard.isValidPlay(validNumberCard, Some(invalidActionCard)) shouldBe false

        val validActionCard2 = ActionCard("green", "draw two")
        val validNumberCard2 = NumberCard("green", 8)
        val invalidNumberCard = NumberCard("yellow", 3)

        //colors match
        gameBoard.isValidPlay(validActionCard2, Some(validNumberCard2)) shouldBe true

        //colors do not match
        gameBoard.isValidPlay(validActionCard2, Some(invalidNumberCard)) shouldBe false
      }
      //case (ActionCard(color, action), ActionCard(topColor, topAction)) => color == topColor || action == topAction
      "accept an action card if it has the same color or action as the top action card" in {
        val topCard = Some(ActionCard("red", "skip"))
        val validCard = ActionCard("red", "reverse")
        val validAction = ActionCard("blue", "skip")
        val invalidCard = ActionCard("blue", "draw two")

        val gameBoard = GameBoard(Nil, Nil)
        gameBoard.isValidPlay(validCard, topCard) shouldBe true
        gameBoard.isValidPlay(validAction, topCard) shouldBe true
        gameBoard.isValidPlay(invalidCard, topCard) shouldBe false
      }
      //case _ => false
      "reject a card with an invalid wildcard type and top card" in {
        val topCard = NumberCard("blue", 5)
        val invalidWildCard = WildCard("unknown")

        val gameBoard = GameBoard(Nil, Nil)
        gameBoard.isValidPlay(invalidWildCard, Some(topCard)) shouldBe false
      }
    }
    //------------------------------------------------------------------------------------------------------------------
    /**test method shuffleDeck*/
    "playing a card" should {

      "handle a number card correctly" in {
        val playerHand = PlayerHand(List(NumberCard("red", 5), NumberCard("blue", 3)))
        val gameBoard = GameBoard(
          drawPile = List(NumberCard("yellow", 9)),
          discardPile = List(NumberCard("red", 3))
        )
        val gameState = GameState(
          players = List(playerHand),
          gameBoard = gameBoard,
          currentPlayerIndex = 0,
          allCards = List.empty
        )

        val updatedGameState = gameBoard.playCard(NumberCard("red", 5), gameState)

        updatedGameState.gameBoard.discardPile.last shouldEqual NumberCard("red", 5)
        updatedGameState.players.head.cards should not contain NumberCard("red", 5)
      }

      "handle a wild card correctly" in {
        val playerHand = PlayerHand(List(WildCard("wild"), NumberCard("blue", 3)))
        val gameBoard = GameBoard(
          drawPile = List(NumberCard("yellow", 9)),
          discardPile = List(NumberCard("red", 3))
        )
        val gameState = GameState(
          players = List(playerHand),
          gameBoard = gameBoard,
          currentPlayerIndex = 0,
          allCards = List.empty
        )

        val updatedGameState = gameBoard.playCard(WildCard("wild"), gameState)

        updatedGameState.gameBoard.discardPile.last shouldEqual WildCard("wild")
        updatedGameState.players.head.cards should not contain WildCard("wild")
      }

      "handle a draw two card correctly" in {
        val playerHand = PlayerHand(List(ActionCard("red", "draw two")))
        val nextPlayerHand = PlayerHand(Nil)
        val gameBoard = GameBoard(
          drawPile = List(NumberCard("yellow", 7), NumberCard("blue", 2)),
          discardPile = List(NumberCard("red", 3))
        )
        val gameState = GameState(
          players = List(playerHand, nextPlayerHand),
          gameBoard = gameBoard,
          currentPlayerIndex = 0,
          allCards = List.empty
        )

        val updatedGameState = gameBoard.playCard(ActionCard("red", "draw two"), gameState)

        updatedGameState.players(1).cards should have size 2
        updatedGameState.players(1).cards should contain allOf(NumberCard("yellow", 7), NumberCard("blue", 2))
      }

      "handle a skip card correctly" in {
        val playerHand = PlayerHand(List(ActionCard("blue", "skip")))
        val nextPlayerHand = PlayerHand(List(NumberCard("red", 9)))
        val skippedPlayerHand = PlayerHand(List(NumberCard("yellow", 6)))
        val gameBoard = GameBoard(
          drawPile = List(NumberCard("green", 2)),
          discardPile = List(NumberCard("blue", 7))
        )
        val gameState = GameState(
          players = List(playerHand, nextPlayerHand, skippedPlayerHand),
          gameBoard = gameBoard,
          currentPlayerIndex = 0,
          allCards = List.empty
        )

        val updatedGameState = gameBoard.playCard(ActionCard("blue", "skip"), gameState)

        updatedGameState.currentPlayerIndex shouldEqual 2 // Next player's turn is skipped
      }

      "handle a reverse card correctly" in {
        val playerHand = PlayerHand(List(ActionCard("red", "reverse")))
        val nextPlayerHand = PlayerHand(List(NumberCard("yellow", 8)))
        val gameBoard = GameBoard(
          drawPile = List(NumberCard("green", 4)),
          discardPile = List(NumberCard("red", 5))
        )
        val gameState = GameState(
          players = List(playerHand, nextPlayerHand),
          gameBoard = gameBoard,
          currentPlayerIndex = 0,
          isReversed = false,
          allCards = List.empty
        )

        val updatedGameState = gameBoard.playCard(ActionCard("red", "reverse"), gameState)

        updatedGameState.isReversed shouldEqual true
        updatedGameState.currentPlayerIndex shouldEqual 1
      }

      "handle a wild draw four card correctly" in {
        val playerHand = PlayerHand(List(WildCard("wild draw four")))
        val nextPlayerHand = PlayerHand(List(NumberCard("blue", 2)))
        val gameBoard = GameBoard(
          drawPile = List.fill(4)(NumberCard("yellow", 1)),
          discardPile = List(NumberCard("red", 7))
        )
        val gameState = GameState(
          players = List(playerHand, nextPlayerHand),
          gameBoard = gameBoard,
          currentPlayerIndex = 0,
          allCards = List.empty
        )

        val updatedGameState = gameBoard.playCard(WildCard("wild draw four"), gameState)

        updatedGameState.players(1).cards should have size 5
      }
      "handle case where no valid card is playable and draw pile is exhausted" in {
        val playerHand = PlayerHand(List(NumberCard("blue", 3)))
        val gameBoard = GameBoard(
          drawPile = List(NumberCard("red", 6)),
          discardPile = List(NumberCard("red", 5))
        )
        val gameState = GameState(
          players = List(playerHand, PlayerHand(List(NumberCard("blue", 7)))),
          gameBoard = gameBoard,
          currentPlayerIndex = 0,
          allCards = List.empty
        )

        val updatedGameState = gameBoard.playCard(NumberCard("blue", 3), gameState)

        updatedGameState.currentPlayerIndex shouldEqual 1
        updatedGameState.players.head.cards should have size 2
      }

      "throw an exception when the iteration count exceeds the maximum limit" in {
        val unplayableCard = NumberCard("green", 7)
        val playerHand = PlayerHand(List(unplayableCard))
        val drawPile = List.fill(200)(NumberCard("blue", 9))
        val gameBoard = GameBoard(
          drawPile = drawPile,
          discardPile = List(NumberCard("red", 5))
        )
        val gameState = GameState(
          players = List(playerHand),
          gameBoard = gameBoard,
          currentPlayerIndex = 0,
          allCards = List.empty
        )

        val exception = intercept[RuntimeException] {
          gameBoard.playCard(unplayableCard, gameState)
        }

        exception.getMessage shouldEqual "Infinite loop detected in playCard logic."
      }
    }
  }
}
