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
    val updatedPlayerHand = playerHand.addCard(drawnCard) 
    (drawnCard, updatedPlayerHand, copy(drawPile = updatedDrawPile))
  }

  def playCard(card: Card, gameState: GameState): GameState = {
    val currentPlayer = gameState.players(gameState.currentPlayerIndex)

    if (!currentPlayer.cards.contains(card)) {
      throw new IllegalArgumentException("Card not in hand")
    }

    val topCard = gameState.gameBoard.discardPile.lastOption
    if (!isValidPlay(card, topCard)) {
      throw new IllegalArgumentException("You can not play this card!")
    }

    val updatedHand = currentPlayer.removeCard(card)
    val updatedDiscardPile = gameState.gameBoard.discardPile :+ card
    val updatedGameBoard = gameState.gameBoard.copy(discardPile = updatedDiscardPile)

    gameState.copy(
      players = gameState.players.updated(gameState.currentPlayerIndex, updatedHand),
      gameBoard = updatedGameBoard
    )
  }

  def isValidPlay(card: Card, topCard: Option[Card]): Boolean = {
    topCard match {
      case None => true 
      case Some(tCard) =>
        (card, tCard) match {
          case (WildCard(_), _) => true
          case (_, WildCard(_)) => true
          case (NumberCard(color, number), NumberCard(topColor, topNumber)) =>
            color == topColor || number == topNumber
          case (NumberCard(color, _), ActionCard(topColor, _)) =>
            color == topColor
          case (ActionCard(color, _), NumberCard(topColor, _)) =>
            color == topColor
          case (ActionCard(color, _), ActionCard(topColor, _)) =>
            color == topColor
          case _ => false
        }
    }
  }
}
