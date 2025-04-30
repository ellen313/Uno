import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec
import model._

class GameStateSpec extends AnyWordSpec {

  "GameState.nextPlayer" should {

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
  }

  "GameState.checkForWinner" should {

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
  }

  "GameState.playerSaysUno" should {

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
  }
}
