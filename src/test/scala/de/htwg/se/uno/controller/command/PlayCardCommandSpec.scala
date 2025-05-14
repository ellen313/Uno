package de.htwg.se.uno.controller.command

import de.htwg.se.uno.model._
import de.htwg.se.uno.controller.GameBoard
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class PlayCardCommandSpec extends AnyFlatSpec with Matchers {

  "PlayCardCommand" should "remove the card from the player's hand and add it to the discard pile if valid" in {
    val initialDiscardPile = List(
      NumberCard("blue", 5),
      NumberCard("blue", 5),
      NumberCard("blue", 5)
    )

    val initialDrawPile = List(
      NumberCard("red", 3),
      NumberCard("green", 8)
    )
    val player1Hand = PlayerHand(List(NumberCard("blue", 7)))
    val player2Hand = PlayerHand(List(NumberCard("red", 3)))

    val gameState = GameState(
      players = List(player1Hand, player2Hand),
      currentPlayerIndex = 0,
      allCards = List(
        NumberCard("blue", 7),
        NumberCard("red", 3),
        NumberCard("blue", 5)
      ),
      isReversed = false,
      discardPile = initialDiscardPile,
      drawPile = initialDrawPile,
      selectedColor = None
    )

    val gameBoard = GameBoard(gameState, initialDrawPile, initialDiscardPile)

    val cardToPlay = NumberCard("blue", 7)
    val playCardCommand = PlayCardCommand(gameBoard, cardToPlay)
    val updatedGameBoard = playCardCommand.execute()
  }
}
