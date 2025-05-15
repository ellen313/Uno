package de.htwg.se.uno.model

import de.htwg.se.uno.util.Observable

case class GameState( players: List[PlayerHand], currentPlayerIndex: Int,
                      allCards: List[Card], isReversed: Boolean = false,
                      discardPile: List[Card], drawPile: List[Card], selectedColor: Option[String] = None)
  extends Observable  {

  def nextPlayer(): GameState= {
    val playerCount = players.length
    val nextIndex = if (isReversed) {
      (currentPlayerIndex - 1 + playerCount) % playerCount
    } else {
      (currentPlayerIndex + 1) % playerCount
    }
    this.copy(currentPlayerIndex = nextIndex)
  }

  def dealInitialCards(cardsPerPlayer: Int): GameState = {
    var updatedGameState = this
    for (_ <- 1 to cardsPerPlayer) {
      for (playerIndex <- updatedGameState.players.indices) {
        val (drawnCard, updatedHand, updatedDrawPile, updatedDiscardPile) =
          updatedGameState.drawCard(updatedGameState.players(playerIndex),
            updatedGameState.drawPile, updatedGameState.discardPile)

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
      hand.isEmpty
    } match {
      case Some((_, winnerIndex)) =>
        Some(winnerIndex)
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

  def playCard(card: Card): GameState = {
    if (players.exists(_.cards.isEmpty)) return this

    val topCard = discardPile.headOption
    if (!isValidPlay(card, topCard)) {
      println("Invalid play.")
      return this
//      var updatedPlayerHand = currentPlayerHand
//      var updatedDrawPile = drawPile
//      var updatedDiscardPile = discardPile
//      var playableCardFound = false
//
//      val maxIterations = 10
//      var iterationCount = 0
//
//      while (!playableCardFound && updatedDrawPile.nonEmpty && iterationCount < maxIterations) {
//        iterationCount += 1
//        val (drawnCard, newHand, newDrawPile, newDiscardPile) =
//          drawCard(updatedPlayerHand, updatedDrawPile, updatedDiscardPile)
//        updatedPlayerHand = newHand
//        updatedDrawPile = newDrawPile
//        updatedDiscardPile = newDiscardPile
//        playableCardFound = isValidPlay(drawnCard, topCard)
    }

    val updatedHand = players(currentPlayerIndex).removeCard(card)
    val updatedDiscardPile = card :: discardPile

    this.copy(
      players = players.updated(currentPlayerIndex, updatedHand),
      discardPile = updatedDiscardPile,
      selectedColor = if (card.isInstanceOf[WildCard]) selectedColor else None,
    )
  }

  def handleDrawCards(count: Int): GameState = {
    val nextPlayerIndex = if (isReversed) {
      (currentPlayerIndex - 1 + players.length) % players.length
    } else {
      (currentPlayerIndex + 1) % players.length
    }

    val (updatedHand, updatedDrawPile, _) =
      (1 to count).foldLeft((players(nextPlayerIndex), drawPile, discardPile)) {
        case ((hand, draw, _), _) =>
          val (_, newHand, newDraw, _) = drawCard(hand, draw, Nil)  // discardPile irrelevant
          (newHand, newDraw, Nil)
      }

    this.copy(
      players = players.updated(nextPlayerIndex, updatedHand),
      drawPile = updatedDrawPile
    )
  }

  def isValidPlay(card: Card, topCard: Option[Card], selectedColor: Option[String] = None): Boolean = {
    topCard match {
      case None => true

      case Some(tCard) =>
//        // top Card wildcard or +2
//        val isFirstTurnException = GameBoard.turnCount <= 1 && (tCard match {
//          case WildCard(_) => true
//          case ActionCard(_, "draw two") => true
//          case _ => false
//        })
//
//        if (isFirstTurnException) return true

        (card, tCard) match {
          case (ActionCard(_, "draw two"), ActionCard(_, "draw two")) => false
          case (WildCard("wild draw four"), WildCard("wild draw four")) => false

          case (NumberCard(color, number), NumberCard(topColor, topNumber)) =>
            color == topColor || number == topNumber

          case (NumberCard(color, _), ActionCard(topColor, _)) =>
            color == topColor

          case (ActionCard(color, _), NumberCard(topColor, _)) =>
            color == topColor

          case (ActionCard(color, action), ActionCard(topColor, topAction)) =>
            color == topColor || action == topAction

          case (WildCard("wild"), _) => true
          case (WildCard("wild draw four"), _) => true
          case (_, WildCard("wild")) => true
          case (_, WildCard("wild draw four")) => true

          case _ => false
        }
    }
  }

  def setSelectedColor(color: String): GameState = {
    this.copy(selectedColor = Some(color))
  }

  def inputHandler(input: String): InputResult = {
    val currentPlayer = players(currentPlayerIndex)

    input match {
      case s"play wild:$index:$color" =>
        try {
          val cardIndex = index.toInt
          if (cardIndex < 0 || cardIndex >= currentPlayer.cards.length)
            return Failure("Invalid card index.")

          currentPlayer.cards(cardIndex) match {
            case wild: WildCard =>
              val playedCard = WildCard(wild.action)
              val updatedGame = 
                setSelectedColor(color)
                playCard(playedCard)
              Success(updatedGame)

            case _ =>
              Failure("Selected card is not a wild card.")
          }
        } catch {
          case _: NumberFormatException =>
            Failure("Card index must be a number.")
        }

      case s"play card:$index" =>
        try {
          val cardIndex = index.toInt
          if (cardIndex < 0 || cardIndex >= currentPlayer.cards.length)
            return Failure("Invalid card index.")

          val card = currentPlayer.cards(cardIndex)
          val topCard = discardPile.lastOption

          if (!isValidPlay(card, topCard)) {
            Failure("Invalid card! Please select a valid card.")
          } else {
            val updatedGame = playCard(card)
            Success(updatedGame)
          }

        } catch {
          case _: NumberFormatException =>
            Failure("Card index must be a number.")
        }

      case _ =>
        Failure("Unknown input command.")
    }
  }


  override def notifyObservers(): Unit = {
    super.notifyObservers()
  }
}