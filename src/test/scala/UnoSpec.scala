import model._
import org.scalatest.matchers.should.Matchers._
import org.scalatest.wordspec.AnyWordSpec

class UnoSpec extends AnyWordSpec {

  "A card" when {
    "created as a NumberCard" should {
      "have a valid color and number" in {
        val card = NumberCard.createNumberCard()
        List("red", "blue", "green", "yellow") should contain(card.color)
        card.number should (be >= 0 and be <= 9)
      }
    }
    "created as a WildCard" should {
      "always have the color 'wild' and a valid action" in {
        val wildCard = WildCard.createWildCard()
        wildCard.color shouldEqual "wild"
        List("wild", "wild draw four") should contain(wildCard.action)
      }
    }
    "created as an ActionCard" should {
      "have a valid color and action" in {
        val card = ActionCard.createActionCard()

        List("red", "blue", "green", "yellow") should contain(card.color)

        val validActions = List("draw two", "reverse", "skip")
        validActions should contain(card.action)
      }
    }
  }

  "The DrawPile" when {
    "creating a random NumberCard" should {
      "return a valid NumberCard object" in {
        val randomCard = NumberCard.createNumberCard()
        List("red", "blue", "green", "yellow") should contain(randomCard.color)
        randomCard.number should (be >= 0 and be <= 9)
      }
    }

    "creating a random WildCard" should {
      "return a valid WildCard object" in {
        val wildCard = WildCard.createWildCard()
        wildCard.color shouldEqual "wild"
        List("wild", "wild draw four") should contain(wildCard.action)
      }
    }
  }

  "A player" when {
    "the player's hand is empty" should {
      "indicate the player has won" in {
        val playerHand = PlayerHand(List())
        playerHand.cards shouldBe empty
      }
    }

    "the player's hand is not empty" should {
      "indicate the player must keep playing" in {
        val playerHand = PlayerHand(List(NumberCard("red", 5)))
        playerHand.cards should not be empty
      }
    }
  }
}
