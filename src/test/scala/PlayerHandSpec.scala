package model

import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

import java.io.{ByteArrayOutputStream, PrintStream}

class PlayerHandSpec extends AnyWordSpec{
  "PlayerHand" should {

    "add a card to the player's hand" in {
      val hand = PlayerHand(List(NumberCard("red", 5)))
      val updatedHand = hand.addCard(NumberCard("blue", 3))
      updatedHand.cards should contain(NumberCard("blue", 3))
      updatedHand.cards should have size 2
      updatedHand.hasSaidUno shouldBe false
    }

    "check if a card is in the player's hand" in {
      val hand = PlayerHand(List(NumberCard("red", 5), NumberCard("blue", 3)))
      hand.containsCard(NumberCard("red", 5)) shouldBe true
      hand.containsCard(NumberCard("yellow", 7)) shouldBe false
    }

    "remove a card from the player's hand" in {
      val hand = PlayerHand(List(NumberCard("red", 5), NumberCard("blue", 3)))
      val updatedHand = hand.removeCard(NumberCard("red", 5))
      updatedHand.cards should not contain NumberCard("red", 5)
      updatedHand.cards should have size 1
      updatedHand.hasSaidUno shouldBe false
    }

    "check if the player has 'Uno'" in {
      val handWithOneCard = PlayerHand(List(NumberCard("red", 5)))
      val handWithTwoCards = PlayerHand(List(NumberCard("red", 5), NumberCard("blue", 3)))

      handWithOneCard.hasUno shouldBe true
      handWithTwoCards.hasUno shouldBe false
    }

    "allow the player to say 'Uno' if they have one card" in {
      val hand = PlayerHand(List(NumberCard("red", 5)))
      val updatedHand = hand.sayUno()
      updatedHand.hasSaidUno shouldBe true
    }

    "not allow the player to say 'Uno' if they have more than one card" in {
      val hand = PlayerHand(List(NumberCard("red", 5), NumberCard("blue", 3)))
      val updatedHand = hand.sayUno()
      updatedHand.hasSaidUno shouldBe false
    }

    "reset the player's 'Uno' status" in {
      val hand = PlayerHand(List(NumberCard("red", 5)), hasSaidUno = true)
      val updatedHand = hand.resetUnoStatus()
      updatedHand.hasSaidUno shouldBe false
    }

    "check if the player's hand is empty" in {
      val emptyHand = PlayerHand(List())
      val nonEmptyHand = PlayerHand(List(NumberCard("red", 5)))

      emptyHand.isEmpty shouldBe true
      nonEmptyHand.isEmpty shouldBe false
    }

    "sort the player's hand by card type and attributes" in {
      val hand = PlayerHand(List(
        ActionCard("blue", "reverse"),
        NumberCard("red", 5),
        WildCard("draw four"),
        NumberCard("blue", 3)
      ))

      val sortedHand = hand.sortHand()

      sortedHand.cards shouldBe List(
        NumberCard("blue", 3),
        NumberCard("red", 5),
        ActionCard("blue", "reverse"),
        WildCard("draw four")
      )
    }

    "display the player's hand" in {
      val hand = PlayerHand(List(
        NumberCard("red", 5),
        ActionCard("blue", "reverse"),
        WildCard("draw four")
      ))
      
      val outCapture = new ByteArrayOutputStream()
      Console.withOut(new PrintStream(outCapture)) {
        hand.displayHand()
      }
      
      val output = outCapture.toString.trim
      output should include("red-5")
      output should include("blue-reverse")
      output should include("draw four")
    }
  
  }
}
