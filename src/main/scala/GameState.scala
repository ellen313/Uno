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
}
