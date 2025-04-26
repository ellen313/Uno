package model

case class GameState( players: List[PlayerHand], currentPlayerIndex: Int,
                      allCards: List[Card], isReversed: Boolean = false,
                      discardPile: List[Card], drawPile: List[Card]){

  def nextPlayer(gameState: GameState): GameState= {
    val playerCount = gameState.players.length
    val nextIndex = if (gameState.isReversed) {
      (gameState.currentPlayerIndex - 1 + playerCount) % playerCount
    } else {
      (gameState.currentPlayerIndex + 1) % playerCount
    }
    gameState.copy(currentPlayerIndex = nextIndex)
  }

  def dealInitialCards(cardsPerPlayer: Int): GameState = {
    var updatedGameState = this
    for (_ <- 1 to cardsPerPlayer) {
      for (playerIndex <- updatedGameState.players.indices) {
        val (drawnCard, updatedHand, updatedBoard) = updatedGameState.drawCard(updatedGameState.players(playerIndex))
        updatedGameState = updatedGameState.copy(
          players = updatedGameState.players.updated(playerIndex, updatedHand),
          gameBoard = updatedBoard)
      }
    }
    updatedGameState
  }

  def checkForWinner(): Option[Int] = {
    players.zipWithIndex.find { case (hand, _) =>
      hand.isEmpty || (hand.hasUno && hand.hasSaidUno)
    } match {
      case Some((_, winnerIndex)) =>
        Some(winnerIndex)
      // no winner found:
      case None =>
        None
    }
  }

  def playerSaysUno(playerIndex: Int): GameState = {
    val updatedPlayers = players.updated(
      playerIndex,
      players(playerIndex).copy(hasSaidUno = true))
    this.copy(players = updatedPlayers)
  }

  def drawCard(playerHand: PlayerHand, drawPile: List[Card]): (Card, PlayerHand) = {
    if (drawPile.isEmpty) {
      if (discardPile.size <= 1) {
        throw new RuntimeException("No cards left in the draw pile")
      } else {
        val reshuffled = scala.util.Random.shuffle(discardPile.init)
        return this.copy(
          drawPile = reshuffled,
          discardPile = List(discardPile.last)
        ).drawCard(playerHand, drawPile = drawPile)
      }
    }

    val drawnCard = drawPile.head
    val updatedDrawPile = drawPile.tail
    val updatedPlayerHand = playerHand + drawnCard
    (drawnCard, updatedPlayerHand, copy(drawPile = updatedDrawPile))
  }

  def playCard(card: Card, gameState: GameState): GameState = {
    val originalPlayerIndex = gameState.currentPlayerIndex
    val currentPlayerHand = gameState.players(originalPlayerIndex)
    val topCard = gameState.gameBoard.discardPile.lastOption

    if (!isValidPlay(card, topCard)) {
      var updatedPlayerHand = currentPlayerHand
      var drawPile = gameState.gameBoard.drawPile
      var playableCardFound = false

      val maxIterations = 10
      var iterationCount = 0

      while (!playableCardFound && drawPile.nonEmpty && iterationCount < maxIterations) {
        iterationCount += 1
        val (drawnCard, newHand, newGameBoard) = gameState.gameBoard.drawCard(updatedPlayerHand)
        updatedPlayerHand = newHand
        drawPile = newGameBoard.drawPile
        playableCardFound = isValidPlay(updatedPlayerHand.cards.last, topCard)
      }

      /*if (!playableCardFound) {
        val resetHand = updatedPlayerHand.resetUnoStatus()
        val updatedGameState = gameState.copy(players = gameState.players.updated(currentPlayerIndex, resetHand))
        return gameState.nextPlayer(updatedGameState)
      }*/

    }

    val updatedHand = currentPlayerHand.removeCard(card)

    val updatedDiscardPile = gameState.gameBoard.discardPile :+ card
    val updatedGameBoard = gameState.gameBoard.copy(discardPile = updatedDiscardPile)

    val baseGameState = gameState.copy(
      players = gameState.players.updated(originalPlayerIndex, updatedHand),
      gameBoard = updatedGameBoard
    )

    //---------------------------------------------- special card ------------------------------------------------------
    val finalGameState = card match {
      //------------ skip ------------
      case ActionCard(_, "skip") =>
        val firstSkipState = baseGameState.nextPlayer(baseGameState)
        val secondSkipState = firstSkipState.nextPlayer(firstSkipState)

        secondSkipState
      //------------ reverse ------------
      case ActionCard(_, "reverse") =>
        val newGameState = baseGameState.copy(isReversed = !baseGameState.isReversed)
        val nextAfterReverse = newGameState.nextPlayer(newGameState)

        nextAfterReverse

      //------------ draw two ------------
      case ActionCard(_, "draw two") =>
        val nextPlayerIndex = baseGameState.nextPlayer(baseGameState).currentPlayerIndex

        val (updatedNextPlayerHand, updatedGameBoard) = (1 to 2).foldLeft((baseGameState.players(nextPlayerIndex),
          baseGameState.gameBoard)) {
          case ((hand, gameBoard), _) =>
            val (drawnCard, updatedHand, updatedGameBoard) = gameBoard.drawCard(hand)
            (updatedHand, updatedGameBoard)
        }

        val updatedGameState = baseGameState.copy(
          players = baseGameState.players.updated(nextPlayerIndex, updatedNextPlayerHand),
          gameBoard = updatedGameBoard,
          currentPlayerIndex = nextPlayerIndex
        )
        updatedGameState

      //------------ wild draw four ------------
      case WildCard("wild draw four") =>
        val nextPlayerIndex = baseGameState.nextPlayer(baseGameState).currentPlayerIndex

        val (updatedNextPlayerHand, updatedGameBoard) = (1 to 4).foldLeft((baseGameState.players(nextPlayerIndex),
          baseGameState.gameBoard)) {
          case ((hand, gameBoard), _) =>
            val (drawnCard, updatedHand, updatedGameBoard) = gameBoard.drawCard(hand)
            (updatedHand, updatedGameBoard)
        }

        val updatedGameState = baseGameState.copy(
          players = baseGameState.players.updated(nextPlayerIndex, updatedNextPlayerHand),
          gameBoard = updatedGameBoard,
          currentPlayerIndex = nextPlayerIndex
        )
        updatedGameState

      //------------ default ------------
      case _ =>
        val updatedGameState = baseGameState.nextPlayer(baseGameState)
        updatedGameState
    }
    val finalHand = if (updatedHand.hasUno) updatedHand.sayUno() else updatedHand.resetUnoStatus()
    finalGameState.copy(
      players = finalGameState.players.updated(originalPlayerIndex, finalHand))
  }

  def isValidPlay(card: Card, topCard: Option[Card], selectedColor: Option[String] = None): Boolean = {
    topCard match {
      case None => true
      case Some(tCard) =>
        (card, tCard) match {

          case (WildCard("wild draw four"), WildCard("wild draw four")) => false
          case (WildCard("wild"), _) => true
          case (WildCard("wild draw four"), _) => true
          case (_, WildCard("wild")) => true
          case (_, WildCard("wild draw four")) => true


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
  
  listener.onStateChanged(this)
}
