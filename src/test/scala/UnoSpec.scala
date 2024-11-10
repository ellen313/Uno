import Main.{GameBoard, PlayerHand, GameState, Card}
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

class UnoSpec extends AnyWordSpec {
  "A card" when {
    "created with a valid number and color" should {
      "return a valid Card object" in {
        val card = Card.numberCard.createNumberCard()
      }
    }
      "created with an invalid number or color" should {
        "return a WildCard object" in {
          val wildCard = Card.WildCard.createRandomCard()
          assert(wildCard.isRight)
          assert(wildCard.right.get.card == "Wild")
        }
      }
    }

  "The DrawPile" when {
    "creating a random card" should {
      "return a valid Card object if the generated number and color are valid" in {
        val randomCard = Card.numberCard.createRandomcard()
        assert(randomCard.isLeft)
        val card = randomCard.left.get
        assert(List("yellow","red", "blue", "green").contains(card.colors))
        //assert(card.number >= 0 && card.number <= 9)
        assert(card.randomNumber)
        assert(card.isSet)
      }

      "return a WildCard object if the generated number or color are invalid" in {
        //Implement test for invalid number of color
        val wildCard = Card.WildCard.createRandomCard()
        assert(wildCard.isRight)
        assert(wildCard.right.get.card == "Wild")
      }
    }
  }

  "A player" when {
    "players hand is empty" should {
      "win" in {
        val playerHand = PlayerHand(List())
        playerHand.isEmpty should be(true)
      }
    }

    "the player's hand is not empty" should {
      "keep on playing" in {
        val playerHand = PlayerHand(List(Card()))
        playerHand.isEmpty should be(false)
      }
    }
  }
}