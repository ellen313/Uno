package de.htwg.se.uno.controller.command

import de.htwg.se.uno.model._
import de.htwg.se.uno.controller.GameBoard
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class PlayCardCommandSpec extends AnyFlatSpec with Matchers {

  "PlayCardCommand" should "remove a valid card from hand and add it to discard pile" in {
    val cardToPlay = NumberCard("blue", 7)

    val player1Hand = PlayerHand(List(cardToPlay))
    val player2Hand = PlayerHand(List(NumberCard("red", 3)))

    val initialDiscardPile = List(NumberCard("blue", 7))
    val initialDrawPile = List(NumberCard("red", 3), NumberCard("green", 8))

    val gameState = GameState(
      players = List(player1Hand, player2Hand),
      currentPlayerIndex = 0,
      allCards = List(cardToPlay, NumberCard("red", 3), NumberCard("green", 8)),
      isReversed = false,
      discardPile = initialDiscardPile,
      drawPile = initialDrawPile,
      selectedColor = None
    )

    val gameBoard = GameBoard(gameState, initialDrawPile, initialDiscardPile)

    val playCardCommand = PlayCardCommand(gameBoard, cardToPlay)
    playCardCommand.execute()

    val updatedHand = gameBoard.gameState.players(0).cards
    val updatedDiscard = gameBoard.gameState.discardPile

    updatedHand should not contain cardToPlay
    updatedDiscard.last shouldEqual cardToPlay
  }

  it should "not change state if the played card is invalid" in {
    val invalidCard = NumberCard("red", 9)
    val topDiscard = NumberCard("blue", 5)

    val player1Hand = PlayerHand(List(invalidCard))
    val player2Hand = PlayerHand(List(NumberCard("red", 3)))

    val discardPile = List(topDiscard)
    val drawPile = List(NumberCard("red", 3), NumberCard("green", 8))

    val gameState = GameState(
      players = List(player1Hand, player2Hand),
      currentPlayerIndex = 0,
      allCards = List(invalidCard, NumberCard("red", 3), NumberCard("green", 8)),
      isReversed = false,
      discardPile = discardPile,
      drawPile = drawPile,
      selectedColor = None
    )

    val gameBoard = GameBoard(gameState, drawPile, discardPile)

    val playCardCommand = PlayCardCommand(gameBoard, invalidCard)
    playCardCommand.execute()

    gameBoard.gameState.players(0).cards should contain(invalidCard)
    gameBoard.gameState.discardPile.last shouldEqual topDiscard
  }

  it should "retain selectedColor when a WildCard is played" in {
    val wildCard = WildCard("wild")

    val player1Hand = PlayerHand(List(wildCard))
    val player2Hand = PlayerHand(List(NumberCard("red", 3)))

    val initialDiscardPile = List(NumberCard("blue", 5))
    val initialDrawPile = List(NumberCard("green", 8))

    val gameState = GameState(
      players = List(player1Hand, player2Hand),
      currentPlayerIndex = 0,
      allCards = List(wildCard, NumberCard("red", 3), NumberCard("blue", 5)),
      isReversed = false,
      discardPile = initialDiscardPile,
      drawPile = initialDrawPile,
      selectedColor = Some("red")
    )

    val gameBoard = GameBoard(gameState, initialDrawPile, initialDiscardPile)

    val command = PlayCardCommand(gameBoard, wildCard)
    command.execute()

    gameBoard.gameState.selectedColor shouldBe Some("red")
    gameBoard.gameState.discardPile.last shouldBe wildCard
  }

  it should "skip the next player when a skip card is played" in {
    val skipCard = ActionCard("blue", "skip")
    val player1Hand = PlayerHand(List(skipCard))
    val player2Hand = PlayerHand(List(NumberCard("red", 3)))
    val player3Hand = PlayerHand(List(NumberCard("yellow", 2)))

    val gameState = GameState(
      players = List(player1Hand, player2Hand, player3Hand),
      currentPlayerIndex = 0,
      allCards = List(skipCard),
      isReversed = false,
      discardPile = List(NumberCard("blue", 5)),
      drawPile = List(),
      selectedColor = None
    )

    val gameBoard = GameBoard(gameState, List(), List(NumberCard("blue", 5)))
    PlayCardCommand(gameBoard, skipCard).execute()

    gameBoard.gameState.currentPlayerIndex shouldBe 2
  }

  it should "reverse play direction and move to previous player when a reverse card is played" in {
    val reverseCard = ActionCard("blue", "reverse")
    val player1Hand = PlayerHand(List(reverseCard))
    val player2Hand = PlayerHand(List(NumberCard("red", 3)))

    val gameState = GameState(
      players = List(player1Hand, player2Hand),
      currentPlayerIndex = 0,
      allCards = List(reverseCard),
      isReversed = false,
      discardPile = List(NumberCard("blue", 5)),
      drawPile = List(),
      selectedColor = None
    )

    val gameBoard = GameBoard(gameState, List(), List(NumberCard("blue", 5)))
    PlayCardCommand(gameBoard, reverseCard).execute()

    gameBoard.gameState.isReversed shouldBe true
    gameBoard.gameState.currentPlayerIndex shouldBe 1
  }

  it should "force the next player to draw two cards and skip their turn when a draw two card is played" in {
    val drawTwo = ActionCard("blue", "draw two")
    val player1Hand = PlayerHand(List(drawTwo))
    val player2Hand = PlayerHand(List())
    val drawPile = List(NumberCard("green", 1), NumberCard("red", 4))

    val gameState = GameState(
      players = List(player1Hand, player2Hand),
      currentPlayerIndex = 0,
      allCards = drawTwo :: drawPile,
      isReversed = false,
      discardPile = List(NumberCard("blue", 5)),
      drawPile = drawPile,
      selectedColor = None
    )

    val gameBoard = GameBoard(gameState, drawPile, List(NumberCard("blue", 5)))
    PlayCardCommand(gameBoard, drawTwo).execute()

    gameBoard.gameState.players(1).cards should have size 2
  }

  it should "force the next player to draw four cards and skip their turn when a wild draw four card is played" in {
    val wildDrawFour = WildCard("wild draw four")
    val player1Hand = PlayerHand(List(wildDrawFour))
    val player2Hand = PlayerHand(List())
    val drawPile = List(NumberCard("green", 1), NumberCard("red", 4), NumberCard("yellow", 9), NumberCard("blue", 2))

    val gameState = GameState(
      players = List(player1Hand, player2Hand),
      currentPlayerIndex = 0,
      allCards = wildDrawFour :: drawPile,
      isReversed = false,
      discardPile = List(NumberCard("red", 5)),
      drawPile = drawPile,
      selectedColor = Some("green")
    )

    val gameBoard = GameBoard(gameState, drawPile, List(NumberCard("red", 5)))
    PlayCardCommand(gameBoard, wildDrawFour).execute()

    gameBoard.gameState.players(1).cards should have size 4
  }
}
