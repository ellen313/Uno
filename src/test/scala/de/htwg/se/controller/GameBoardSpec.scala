package controller

import org.scalatest.matchers.should.Matchers.*
import org.scalatest.wordspec.AnyWordSpec

class GameBoardSpec extends AnyWordSpec {

  class EmptyDeckGameBoard extends GameBoard (GameState(Nil, 0, Nil, isReversed = false, Nil, Nil),
    Nil, Nil
  ) {
    override def createDeckWithAllCards(): List[Card] = Nil
  }

  "A GameBoard with an empty deck" should {
    "return empty drawPile and discardPile when shuffled" in {
      val emptyGameBoard = new EmptyDeckGameBoard()
      val shuffled = emptyGameBoard.shuffleDeck()

      shuffled.drawPile shouldBe empty
      shuffled.discardPile shouldBe empty
    }
  }
}
