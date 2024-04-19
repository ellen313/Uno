import Main.{GameBoard, PlayerHand, printGameBoard}
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

class CardSpec extends AnyWordSpec {
  "A card" when {
    "created with a valid number and color" should {
      "return a valid Card object" in {
        val card = Cards.Card("red", 2)
        assert(card.color == "red")
        assert(card.number == 5)
        assert(card.isSet)
      }
    }
      "created with an invalid number or color" should {
        "return a SkipBoCard object" in {
          val skipBoCard = Cards.CardFactory.createRandomCard()
          assert(skipBoCard.isRight)
          assert(skipBoCard.right.get.card == "SkipBo")
        }
      }
    }

  "The CardFactory" when {
    "creating a random card" should {
      "return a valid Card object if the generated number and color are valid" in {
        val randomCard = Cards.CardFactory.createRandomcard()
        assert(randomCard.isLeft)
        val card = randomCard.left.get
        assert(List("red", "blue", "green").contains(card.color))
        assert(card.number >= 1 && card.number <= 12)
        assert(card.isSet)
      }

      "return a SkipBoCard object if the generated number or color are invalid" in {
        //Implement test for invalid number of color
        val skipBoCard = Cards.CardFactory.createRandomCard()
        assert(skipBoCard.isRight)
        assert(skipBoCard.right.get.card == "SkipBo")
      }
    }
  }
}