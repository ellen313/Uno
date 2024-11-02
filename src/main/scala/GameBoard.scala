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

  def playCard(card: Card, gameState: GameState): GameState = {
    val currentPlayer = gameState.players(gameState.currentPlayerIndex)

    if (!currentPlayer.cards.contains(card)) { //check für später bei benutzereingabe
      throw new IllegalArgumentException("Card not in hand")
    }

    val topCard = gameState.gameBoard.discardPile.lastOption
    if (!isValidPlay(card, topCard)) {
      throw new IllegalArgumentException("You can not play this card!")
    }

    val updatedHand = currentPlayer.removeCard(card)
    val updatedDiscardPile = gameState.gameBoard.discardPile :+ card
    val updatedGameBoard = gameState.gameBoard.copy(discardPile = updatedDiscardPile)

    val baseGameState = gameState.copy(
      players = gameState.players.updated(gameState.currentPlayerIndex, updatedHand),
      gameBoard = updatedGameBoard
    )
    val finalGameState = card match {
      case ActionCard(_, "reverse") => baseGameState.nextPlayer(baseGameState.copy(isReversed = !gameState.isReversed))
      case ActionCard(_, "skip") => baseGameState.nextPlayer(baseGameState.nextPlayer(baseGameState))

      case ActionCard(_, "draw two") =>
        val nextIndex = baseGameState.nextPlayer(baseGameState).currentPlayerIndex
        val updatedNextPlayerHand = (1 to 2).foldLeft(baseGameState.players(nextIndex)) { (hand, _) =>
          baseGameState.gameBoard.drawCard(hand)._2
        }
        baseGameState.copy(players = baseGameState.players.updated(nextIndex, updatedNextPlayerHand))
      case WildCard("wild draw four") =>
        val nextIndex = baseGameState.nextPlayer(baseGameState).currentPlayerIndex
        val updatedNextPlayerHand = (1 to 4).foldLeft(baseGameState.players(nextIndex)) { (hand, _) =>
          baseGameState.gameBoard.drawCard(hand)._2
        }
        baseGameState.copy(players = baseGameState.players.updated(nextIndex, updatedNextPlayerHand))
      case _ => baseGameState.nextPlayer(baseGameState)
    }
    finalGameState
  }

  def isValidPlay(card: Card, topCard: Option[Card]): Boolean = {
    topCard match {
      case None => true
      case Some(tCard) =>
        (card, tCard) match {

          case (WildCard("wild draw four"), WildCard("wild draw four")) => false
          case (WildCard(_), _) => true
          case (_, WildCard(_)) => true
          case (ActionCard(color, "draw two"), ActionCard(topColor, "draw two")) => color == topColor

          case (NumberCard(color, number), NumberCard(topColor, topNumber)) =>
            color == topColor || number == topNumber

          case (NumberCard(color, _), ActionCard(topColor, _)) => color == topColor
          case (ActionCard(color, _), NumberCard(topColor, _)) => color == topColor

          case (ActionCard(color, action), ActionCard(topColor, topAction)) =>
            color == topColor || action == topAction

          case _ => false
        }
    }
  }
}
