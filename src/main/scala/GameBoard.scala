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
    var updatedPlayerHand = gameState.players(gameState.currentPlayerIndex)
    var drawPile = gameState.gameBoard.drawPile
    val topCard = gameState.gameBoard.discardPile.lastOption
    var playableCardFound = isValidPlay(card, topCard)
    var drawCount = 0 
    
    while (!playableCardFound && drawPile.nonEmpty && drawCount < 10) {
      val (drawnCard, newHand, newGameBoard) = gameState.gameBoard.drawCard(updatedPlayerHand)
      updatedPlayerHand = newHand
      drawPile = newGameBoard.drawPile
      playableCardFound = isValidPlay(updatedPlayerHand.cards.last, topCard)
      drawCount += 1
    }
    
    if (!playableCardFound) {
      return gameState.nextPlayer(gameState)
    }
    
    val updatedHand = updatedPlayerHand.removeCard(updatedPlayerHand.cards.last)
    
    val updatedDiscardPile = gameState.gameBoard.discardPile :+ updatedPlayerHand.cards.last
    val updatedGameBoard = gameState.gameBoard.copy(discardPile = updatedDiscardPile)
    
    val baseGameState = gameState.copy(
      players = gameState.players.updated(gameState.currentPlayerIndex, updatedHand),
      gameBoard = updatedGameBoard
    )
    
    val finalGameState = updatedPlayerHand.cards.last match {
      case ActionCard(_, "reverse") =>
        baseGameState.copy(isReversed = !gameState.isReversed)
        
      case ActionCard(_, "skip") => baseGameState.nextPlayer(baseGameState.nextPlayer(baseGameState))
      
      case ActionCard(_, "draw two") =>
        val nextIndex = baseGameState.nextPlayer(baseGameState).currentPlayerIndex
        val (updatedNextPlayerHand, updatedGameBoard) = (1 to 2).foldLeft((baseGameState.players(nextIndex), baseGameState.gameBoard)) {
          case ((hand, gameBoard), _) =>
            val (drawnCard, updatedHand, updatedUpdatedGameBoard) = gameBoard.drawCard(hand)
            (updatedHand, updatedUpdatedGameBoard)
        }
        val updatedGameState = baseGameState.copy(
          players = baseGameState.players.updated(nextIndex, updatedNextPlayerHand),
          gameBoard = updatedGameBoard,
          currentPlayerIndex = nextIndex
        )
        val updatedGameStateAfterDrawTwo = updatedGameState.nextPlayer(updatedGameState)
        updatedGameStateAfterDrawTwo
        
      case WildCard("wild draw four") =>
        val nextIndex = baseGameState.nextPlayer(baseGameState).currentPlayerIndex
        val (updatedNextPlayerHand, updatedGameBoard) = (1 to 4).foldLeft((baseGameState.players(nextIndex), baseGameState.gameBoard)) {
          case ((hand, gameBoard), _) =>
            val (drawnCard, updatedHand, updatedUpdatedGameBoard) = gameBoard.drawCard(hand)
            (updatedHand, updatedUpdatedGameBoard)
        }
        baseGameState.copy(
          players = baseGameState.players.updated(nextIndex, updatedNextPlayerHand),
          gameBoard = updatedGameBoard,
          currentPlayerIndex = nextIndex
        )
        
      case _ =>
        baseGameState.nextPlayer(baseGameState)
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
