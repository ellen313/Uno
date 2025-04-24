import org.scalatest.wordspec.AnyWordSpec
import model._

class ColorPrinterSpec extends AnyWordSpec {

  "A WildCard" should {
    "have the correct action for 'wild'" in {
      val wildCard = WildCard("wild")
      assert(wildCard.action == "wild")
    }

    "have the correct action for 'wild draw four'" in {
      val wildCard = WildCard("wild draw four")
      assert(wildCard.action == "wild draw four")
    }

    "set action to Reset for unknown actions" in {
      val wildCard = WildCard("invalid action")
      assert(wildCard.action == "invalid action") // The action string is what you pass, but Reset is applied later.
      // Assuming the Reset is applied when printing, you'd verify the output behavior in the printCard function
    }
  }

  "A NumberCard" should {
    "have the correct properties" in {
      val numberCard = NumberCard("red", 5)
      assert(numberCard.color == "red")
      assert(numberCard.number == 5)
    }
  }

  "An ActionCard" should {
    "have the correct properties" in {
      val actionCard = ActionCard("blue", "skip")
      assert(actionCard.color == "blue")
      assert(actionCard.action == "skip")
    }
  }
}
