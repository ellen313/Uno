package model

case class GameState( players: List[PlayerHand], gameBoard: GameBoard, currentPlayerIndex: Int,
                      allCards: List[Card], isReversed: Boolean = false){

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
        val (drawnCard, updatedHand, updatedBoard) = updatedGameState.gameBoard.drawCard(updatedGameState.players(playerIndex))
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
        println(s"Player ${winnerIndex + 1} wins!")
        Some(winnerIndex)
      // no winner found:
      case None =>
        None
    }
  }

  def playerSaysUno(playerIndex: Int): GameState = {
    val updatedPlayers = players.updated(
      playerIndex,
      players(playerIndex).sayUno()
    )
    copy(players = updatedPlayers)
  }
}
