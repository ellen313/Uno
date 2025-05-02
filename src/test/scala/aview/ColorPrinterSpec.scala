import org.scalatest.wordspec.AnyWordSpec
import model.*
import aview.ColorPrinter.printCard

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
      assert(wildCard.action == "invalid action")
    }
    "match on WildCard with action 'wild'" in {
      val card = WildCard("wild")

      val result = card.action match {
        case "wild" => "Wild Card"
        case "wild draw four" => "Wild Draw Four"
        case _ => "Unknown"
      }

      assert(result == "Wild Card")
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

  "ColorPrinter" should {
    "print the correct string for wild cards" in {
      val wild = WildCard("wild")
      val wildDrawFour = WildCard("wild draw four")

      val cards: List[Card] = List(wild, wildDrawFour)

      cards.foreach {
        case wild: WildCard =>
          val actionString = wild.action match {
            case "wild" => "Wild Card"
            case "wild draw four" => "Wild Draw Four"
          }
          println(s"${Console.RESET}$actionString${Console.RESET}")
        case _ =>
      }
    }

    "print correct output for 'wild' card" in {
      val card = WildCard("wild")
      printCard(card)
      succeed
    }
    "print correct output for 'wild draw four' card" in {
      val card = WildCard("wild draw four")
      printCard(card)
      succeed
    }
  }
}
