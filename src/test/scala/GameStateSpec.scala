package model

import controller.GameBoard
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

class GameStateSpec extends AnyWordSpec{
  "move to the next player correctly when isReversed is false" in {
    val gameState = GameState(
      players = List(PlayerHand(List()), PlayerHand(List()), PlayerHand(List())),
      gameBoard = GameBoard(
        drawPile = List(NumberCard("red", 1), NumberCard("blue", 2)),
        discardPile = List()
      ),
      currentPlayerIndex = 0,
      allCards = List(),
      isReversed = false
    )

    val updatedGameState = gameState.nextPlayer(gameState)
    updatedGameState.currentPlayerIndex shouldBe 1
  }

  "move to the previous player correctly when isReversed is true" in {
    val gameState = GameState(
      players = List(PlayerHand(List()), PlayerHand(List()), PlayerHand(List())),
      gameBoard = GameBoard(
        drawPile = List(NumberCard("red", 1), NumberCard("blue", 2)),
        discardPile = List()
      ),
      currentPlayerIndex = 0,
      allCards = List(),
      isReversed = true
    )

    val updatedGameState = gameState.nextPlayer(gameState)
    updatedGameState.currentPlayerIndex shouldBe 2
  }

  "deal initial cards to all players" in {
    val initialDeck = List(
      NumberCard("red", 1),
      NumberCard("blue", 2),
      NumberCard("green", 3),
      NumberCard("yellow", 4),
      NumberCard("red", 5),
      NumberCard("blue", 6)
    )
    val gameBoard = GameBoard(
      drawPile = initialDeck,
      discardPile = List()
    )
    val gameState = GameState(
      players = List(PlayerHand(List()), PlayerHand(List())),
      gameBoard = gameBoard,
      currentPlayerIndex = 0,
      allCards = initialDeck
    )

    val updatedGameState = gameState.dealInitialCards(2)

    updatedGameState.players.foreach { hand =>
      hand.cards should have size 2
    }
    updatedGameState.gameBoard.drawPile should have size 2
  }

  "detect a winner when a player has an empty hand" in {
    val gameState = GameState(
      players = List(
        PlayerHand(List(NumberCard("red", 1))),
        PlayerHand(List())
      ),
      gameBoard = GameBoard(
        drawPile = List(),
        discardPile = List()
      ),
      currentPlayerIndex = 0,
      allCards = List()
    )

    val winner = gameState.checkForWinner()
    winner shouldBe Some(1)
  }

  "detect a winner when a player has one card and says 'Uno'" in {
    val gameState = GameState(
      players = List(
        PlayerHand(List(NumberCard("red", 1)), hasSaidUno = true),
        PlayerHand(List(NumberCard("blue", 2), NumberCard("green", 3)))
      ),
      gameBoard = GameBoard(
        drawPile = List(),
        discardPile = List()
      ),
      currentPlayerIndex = 0,
      allCards = List()
    )

    val winner = gameState.checkForWinner()
    winner shouldBe Some(0)
  }

  "not detect a winner when no player meets winning conditions" in {
    val gameState = GameState(
      players = List(
        PlayerHand(List(NumberCard("red", 1), NumberCard("blue", 2))),
        PlayerHand(List(NumberCard("green", 3)))
      ),
      gameBoard = GameBoard(
        drawPile = List(),
        discardPile = List()
      ),
      currentPlayerIndex = 0,
      allCards = List()
    )

    val winner = gameState.checkForWinner()
    winner shouldBe None
  }

  "allow a player to say 'Uno'" in {
    val gameState = GameState(
      players = List(
        PlayerHand(List(NumberCard("red", 1))),
        PlayerHand(List(NumberCard("blue", 2), NumberCard("green", 3)))
      ),
      gameBoard = GameBoard(
        drawPile = List(),
        discardPile = List()
      ),
      currentPlayerIndex = 0,
      allCards = List()
    )

    val updatedGameState = gameState.playerSaysUno(0)
    updatedGameState.players(0).hasSaidUno shouldBe true
  }

  "draw a card correctly from the draw pile" in {
    val initialDeck = List(NumberCard("red", 1), NumberCard("blue", 2), NumberCard("green", 3))
    val gameBoard = GameBoard(
      drawPile = initialDeck,
      discardPile = List()
    )
    val gameState = GameState(
      players = List(PlayerHand(List()), PlayerHand(List())),
      gameBoard = gameBoard,
      currentPlayerIndex = 0,
      allCards = initialDeck
    )
    
    val (drawnCard, updatedHand, updatedBoard) = gameState.gameBoard.drawCard(gameState.players.head)

    drawnCard shouldBe NumberCard("red", 1)
    updatedHand.cards should contain(NumberCard("red", 1))
    updatedBoard.drawPile should not contain (NumberCard("red", 1))
    updatedBoard.drawPile should have size 2
  }
}
