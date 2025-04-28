package model

case class GameState( players: List[PlayerHand], currentPlayerIndex: Int,
                      allCards: List[Card], isReversed: Boolean = false,
                      discardPile: List[Card], drawPile: List[Card]) extends Observable  {

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
        val drawPile = updatedGameState.drawPile
        val discardPile = updatedGameState.discardPile
        val (drawnCard, updatedHand, updatedDrawPile, updatedDiscardPile) =
          updatedGameState.drawCard(updatedGameState.players(playerIndex), drawPile, discardPile)
        updatedGameState = updatedGameState.copy(
          players = updatedGameState.players.updated(playerIndex, updatedHand),
          drawPile = updatedDrawPile,
          discardPile = updatedDiscardPile
        )
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

  def drawCard(playerHand: PlayerHand, drawPile: List[Card], discardPile: List[Card]):
  (Card, PlayerHand, List[Card], List[Card]) = {
    if (drawPile.isEmpty) {
      if (discardPile.size <= 1) {
        throw new RuntimeException("No cards left in the draw pile")
      } else {
        val reshuffled = scala.util.Random.shuffle(discardPile.init)
        return drawCard(playerHand, reshuffled, List(discardPile.last))
      }
    }

    val drawnCard = drawPile.head
    val updatedDrawPile = drawPile.tail
    val updatedPlayerHand = playerHand + drawnCard
    (drawnCard, updatedPlayerHand, updatedDrawPile, discardPile)
  }

  def playCard(card: Card, gameState: GameState): GameState = {
    val originalPlayerIndex = gameState.currentPlayerIndex
    val currentPlayerHand = gameState.players(originalPlayerIndex)
    val topCard = discardPile.lastOption

    if (!isValidPlay(card, topCard)) {
      var updatedPlayerHand = currentPlayerHand
      var drawPile = gameState.drawPile
      var discardPile = gameState.discardPile
      var playableCardFound = false

      val maxIterations = 10
      var iterationCount = 0

      while (!playableCardFound && drawPile.nonEmpty && iterationCount < maxIterations) {
        iterationCount += 1
        val (drawnCard, newHand, updatedDrawPile, updatedDiscardPile) = drawCard(updatedPlayerHand, drawPile, discardPile)
        updatedPlayerHand = newHand
        drawPile = updatedDrawPile
        discardPile = updatedDiscardPile
        playableCardFound = isValidPlay(updatedPlayerHand.cards.last, topCard)
      }

      gameState.copy(players = gameState.players.updated(originalPlayerIndex, updatedPlayerHand))
    }

    val updatedHand = currentPlayerHand.removeCard(card)

    val updatedDiscardPile = discardPile :+ card
    val updatedGameBoard = copy(discardPile = updatedDiscardPile)

    val baseGameState = gameState.copy(
      players = gameState.players.updated(originalPlayerIndex, updatedHand),
      drawPile = updatedGameBoard.drawPile,
      discardPile = updatedGameBoard.discardPile
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

        val updatedDrawPile = baseGameState.drawPile
        val updatedDiscardPile = baseGameState.discardPile

        val (updatedNextPlayerHand, updatedGameBoard) = (1 to 2).foldLeft[(PlayerHand, (List[Card], List[Card]))](
          (baseGameState.players(nextPlayerIndex),
          (baseGameState.drawPile, baseGameState.discardPile))) {
          case ((hand, (drawPile, discardPile)), _) =>
            val (drawnCard, updatedHand, updatedDrawPile, updatedDiscardPile) = drawCard(hand, drawPile, discardPile)
            (updatedHand, (updatedDrawPile, updatedDiscardPile))
        }

        val updatedGameState = baseGameState.copy(
          players = baseGameState.players.updated(nextPlayerIndex, updatedNextPlayerHand),
          drawPile = updatedDrawPile,
          discardPile = updatedDiscardPile,
          currentPlayerIndex = nextPlayerIndex
        )
        updatedGameState

      //------------ wild draw four ------------
      case WildCard("wild draw four") =>
        val nextPlayerIndex = baseGameState.nextPlayer(baseGameState).currentPlayerIndex

        val updatedDrawPile = baseGameState.drawPile
        val updatedDiscardPile = baseGameState.discardPile

        val (updatedNextPlayerHand, updatedGameBoard) = (1 to 4).foldLeft((baseGameState.players(nextPlayerIndex),
          (baseGameState.drawPile, baseGameState.discardPile))) {
          case ((hand, (drawPile, discardPile)), _) =>
            val (drawnCard, updatedHand, updatedDrawPile, updatedDiscardPile) = drawCard(hand, drawPile, discardPile)
            (updatedHand, (updatedDrawPile, updatedDiscardPile))
        }

        val updatedGameState = baseGameState.copy(
          players = baseGameState.players.updated(nextPlayerIndex, updatedNextPlayerHand),
          drawPile = updatedDrawPile,
          discardPile = updatedDiscardPile,
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
}
