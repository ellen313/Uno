import scala.util.Random

case class GameBoard(drawPile: List[Card], discardPile: List[Card]) {
  def createDeckWithAllCards(): List[Card] = {
    val numberCards = for {
      // 1x0 and 2x1-9 for each color
      number <- 0 to 9
      color <- List("yellow", "red", "blue", "green")
    } yield {
      if (number == 0) {
        List(NumberCard(color, number))
      } else {
        List(
          NumberCard(color, number), //1. card
          NumberCard(color, number) //2. card
        )
      }
    }
    val allNumberCards = numberCards.flatten.toList 

    val actionCards = List("draw two", "skip", "reverse").flatMap { action =>
      List(
        ActionCard("red", action),
        ActionCard("blue", action),
        ActionCard("green", action),
        ActionCard("yellow", action)
      )
    }
    val wildCards = List.fill(4)(WildCard("wild")) ++ List.fill(4)(WildCard("wild draw four"))
    allNumberCards ++ actionCards ++ wildCards
  }

  def shuffleDeck(): GameBoard = {
    val allCards = createDeckWithAllCards() 
    val shuffledCards = Random.shuffle(allCards)
    GameBoard(shuffledCards, List.empty[Card]) 
  }

  def drawCard(playerHand: PlayerHand): (Card, PlayerHand, GameBoard) = {
    if (drawPile.isEmpty) {
      throw new RuntimeException("No cards left in the draw pile")
    }
    val drawnCard = drawPile.head 
    val updatedDrawPile = drawPile.tail 
    //val updatedPlayerHand = playerHand.addCard(drawnCard)
    val updatedPlayerHand = playerHand + drawnCard
    (drawnCard, updatedPlayerHand, copy(drawPile = updatedDrawPile))
  }
}
