import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.should.Matchers.*
import de.htwg.se.uno.model.*
import de.htwg.se.uno.model.cardComponent.{Card, WildCard}

class CardSpec extends AnyWordSpec {

  "Card object" should {

    "have defined colors" in {
      Card.colors should contain allOf ("red", "blue", "green", "yellow")
    }

    "create a WildCard with a valid action using apply" in {
      val card = Card("wild")
      card shouldBe a [WildCard]
      Card.actions should contain(card.asInstanceOf[WildCard].action)
    }

    "directly create a WildCard using createWildCard" in {
      val wildCard = Card.createWildCard()
      wildCard shouldBe a [WildCard]
      Card.actions should contain(wildCard.action)
    }
  }
}
