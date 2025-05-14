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
    playCardCommand.execute() // gibt nichts zurück

    // Jetzt prüfen wir den Zustand in `gameBoard` nach dem Ausführen
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

    // Sollte unverändert sein
    gameBoard.gameState.players(0).cards should contain(invalidCard)
    gameBoard.gameState.discardPile.last shouldEqual topDiscard
  }
}
